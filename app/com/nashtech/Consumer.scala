package com.nashtech


import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.Order
import com.nashtech.order.v1.models.json.jsonReadsOrderOrder
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler
import software.amazon.kinesis.lifecycle.events._
import software.amazon.kinesis.processor.{ShardRecordProcessor, ShardRecordProcessorFactory}
import software.amazon.kinesis.retrieval.KinesisClientRecord

import java.io.{BufferedReader, IOException, InputStreamReader}
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.{ExecutionException, TimeUnit, TimeoutException}
import javax.inject.Inject
import scala.util.{Failure, Success, Try}

class OrderEventProcessorFactory @Inject() (ordersDao: OrdersDao) extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new OrderEventProcessor(ordersDao)
}

class OrderEventConsumer @Inject() (
                                ordersDao: OrdersDao
                              ) extends LazyLogging {


  def run(kinesisClient: KinesisAsyncClient): Unit = {;
    val credentials = AwsBasicCredentials.create("test", "test")

    val credentialsProvider = StaticCredentialsProvider.create(credentials)
    // println(s"1111111111111111111111111111111111")
    val dynamoClient = DynamoDbAsyncClient
      .builder()
      .region(Region.US_EAST_1)
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .build()

    // println(s"22222222222222222222222222222222222")
    val cloudWatchClient = CloudWatchAsyncClient
      .builder()
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .region(Region.US_EAST_1)
      .build()
    // println(s"33333333333333333333333333333333333333333333333")
    val configsBuilder = new ConfigsBuilder(
      "order-stream",
      "order-application",
      kinesisClient,
      dynamoClient,
      cloudWatchClient,
      UUID.randomUUID.toString,
      new OrderEventProcessorFactory(ordersDao)
    )
    // println(s"4444444444444444444444444444444444444444")

    val scheduler: Scheduler = new Scheduler(
      configsBuilder.checkpointConfig,
      configsBuilder.coordinatorConfig,
      configsBuilder.leaseManagementConfig,
      configsBuilder.lifecycleConfig,
      configsBuilder.metricsConfig,
      configsBuilder.processorConfig,
      configsBuilder.retrievalConfig
    )
    // println(s"55555555555555555555555555555555555555555555555555")

    val schedulerThread = new Thread(scheduler)
    schedulerThread.setDaemon(true)
    schedulerThread.start()
    // println(s"666666666666666666666666666666666666666666")
    val reader = new BufferedReader(new InputStreamReader(System.in))

    try {
      // println(s"7777777777777777777777777777777777777777777")
      reader.readLine
    }
    catch {
      case ioException: IOException =>
        // println(s"8888888888888888888888888888888888888")

        println("Caught exception while waiting for confirm. Shutting down.", ioException)
    }

    val gracefulShutdownFuture = scheduler.startGracefulShutdown
    println("Waiting up to 20 seconds for shutdown to complete.")
    try {
      // println(s"99999999999999999999999999999999999999999")
      gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
    }
    catch {
      case _: InterruptedException =>
        // println(s"101010101010101010101010101010101010101010")
        println("Interrupted while waiting for graceful shutdown. Continuing.")
      case e: ExecutionException =>
        // println(s"11-11-11-11-11-11-11-11-11-11-11-11")
        println("Exception while executing graceful shutdown.", e)
      case _: TimeoutException =>
        // println(s"12121212121212121212121212121212121212121212")
        println("Timeout while waiting for shutdown. Scheduler may not have exited.")
    }
    // println(s"13131313131313131313131313131313131313131313")

    println("Completed, shutting down now.")

  }
}

class OrderEventProcessor @Inject() (dao: OrdersDao) extends ShardRecordProcessor with LazyLogging {

  override def initialize(initializationInput: InitializationInput): Unit = {
    println(s"Initializing record processor for shard: ${initializationInput.shardId}")
    println(s"Initializing @ Sequence: ${initializationInput.extendedSequenceNumber.toString}")
  }

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit =
    try {
      logger.info("Processing " + processRecordsInput.records.size + " record(s)")
      processRecordsInput.records.forEach((r: KinesisClientRecord) => processRecord(r))
    } catch {
      case _: Throwable =>
        logger.error("Caught throwable while processing records. Aborting.")
        Runtime.getRuntime.halt(1)
    }

  private def processRecord(record: KinesisClientRecord): Unit = {
    val eventString = StandardCharsets.UTF_8.decode(record.data).toString
    println(
      s"Processing record pk: ${record.partitionKey()} -- Data: $eventString"
    )
    val eventJson = Json.parse(eventString)
    val event = Try(eventJson.as[Order])

    event match {
      case Success(order) => println(s"Consumed Order. $order")
      case Failure(e) => println(s"Failed to consume. $e")
    }
  }

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit =
    println("Lost lease, so terminating.")

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit =
    try {
      // Important to checkpoint after reaching end of shard, so to start processing data from child shards.
      println("Reached shard end checkpointing.")
      shardEndedInput.checkpointer.checkpoint()
    } catch {
      case e: Throwable =>
        println("Exception while checkpointing at shard end. Giving up.", e)
    }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit =
    try {
      println("Scheduler is shutting down, checkpointing.")
      shutdownRequestedInput.checkpointer().checkpoint()
    } catch {
      case e: Throwable =>
        println("Exception while checkpointing at requested shutdown. Giving up.", e)
    }
}