package com.nashtech


import com.nashtech.database.OrdersDao
import com.nashtech.order.v1.models.Order
import com.nashtech.order.v1.models.json.jsonReadsOrderOrder
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
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
                                applicationName: String,
                                ordersDao: OrdersDao
                              ) extends LazyLogging {


  def run(kinesisClient: KinesisAsyncClient): Unit = {
    val dynamoClient = DynamoDbAsyncClient.builder().region(Region.US_EAST_1).build()
    val cloudWatchClient = CloudWatchAsyncClient.builder().region(Region.US_EAST_1).build()
    val configsBuilder = new ConfigsBuilder(
      "order-stream",
      applicationName,
      kinesisClient,
      dynamoClient,
      cloudWatchClient,
      UUID.randomUUID.toString,
      new OrderEventProcessorFactory(ordersDao)
    )

    val scheduler = new Scheduler(
      configsBuilder.checkpointConfig,
      configsBuilder.coordinatorConfig,
      configsBuilder.leaseManagementConfig,
      configsBuilder.lifecycleConfig,
      configsBuilder.metricsConfig,
      configsBuilder.processorConfig,
      configsBuilder.retrievalConfig
    )

    val schedulerThread = new Thread(scheduler)
    schedulerThread.setDaemon(true)
    schedulerThread.start()

    val reader = new BufferedReader(new InputStreamReader(System.in))

    try reader.readLine
    catch {
      case ioException: IOException =>
        logger.error("Caught exception while waiting for confirm. Shutting down.", ioException)
    }

    val gracefulShutdownFuture = scheduler.startGracefulShutdown
    logger.info("Waiting up to 20 seconds for shutdown to complete.")
    try gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
    catch {
      case _: InterruptedException =>
        logger.info("Interrupted while waiting for graceful shutdown. Continuing.")
      case e: ExecutionException =>
        logger.error("Exception while executing graceful shutdown.", e)
      case _: TimeoutException =>
        logger.error("Timeout while waiting for shutdown. Scheduler may not have exited.")
    }
    logger.info("Completed, shutting down now.")

  }
}

class OrderEventProcessor @Inject() (dao: OrdersDao) extends ShardRecordProcessor with LazyLogging {

  override def initialize(initializationInput: InitializationInput): Unit = {
    logger.info(s"Initializing record processor for shard: ${initializationInput.shardId}")
    logger.info(s"Initializing @ Sequence: ${initializationInput.extendedSequenceNumber.toString}")
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
    logger.info(
      s"Processing record pk: ${record.partitionKey()} -- Data: $eventString"
    )
    val eventJson = Json.parse(eventString)
    val event = Try(eventJson.as[Order])

    event match {
      case Success(order) => logger.info(s"Consumed Order. $order")
      case Failure(e) => logger.info(s"Failed to consume. $e")
    }
  }

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit =
    logger.info("Lost lease, so terminating.")

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit =
    try {
      // Important to checkpoint after reaching end of shard, so to start processing data from child shards.
      logger.info("Reached shard end checkpointing.")
      shardEndedInput.checkpointer.checkpoint()
    } catch {
      case e: Throwable =>
        logger.error("Exception while checkpointing at shard end. Giving up.", e)
    }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit =
    try {
      logger.info("Scheduler is shutting down, checkpointing.")
      shutdownRequestedInput.checkpointer().checkpoint()
    } catch {
      case e: Throwable =>
        logger.error("Exception while checkpointing at requested shutdown. Giving up.", e)
    }
}