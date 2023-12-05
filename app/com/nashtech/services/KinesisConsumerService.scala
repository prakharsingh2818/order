package com.nashtech.services

import com.nashtech.OrderEventConsumer
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model._

import javax.inject.{Inject, Singleton}
import scala.Console.println
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.SECONDS

@Singleton
class KinesisConsumerService @Inject() (consumer: OrderEventConsumer) {
  initialize()

  private def createStream(kinesisClient: KinesisAsyncClient, streamName: String, numAttempts: Int = 0): CreateStreamResponse = {
    // println("---------------------------")
    val response = kinesisClient.createStream(
        CreateStreamRequest.builder()
          .streamName(streamName)
          .shardCount(1)
          .build())
      .get(10, SECONDS)

    val stream = kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build())
    //    println("\n***************************"+stream+"\n")

    val streamNameDesc = stream.get.streamDescription().streamName()
    System.setProperty("aws.cborEnabled", "false")
    val shardId = stream.get().streamDescription().shards().get(0).shardId()

    val getShardIteratorRequest = GetShardIteratorRequest.builder()
      .streamName(streamNameDesc)
      .shardId(shardId)
      .shardIteratorType(ShardIteratorType.LATEST)
      .build()

    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).get().getValueForField("ShardIterator", classOf[String])
    println(s"\n\n$shardIterator\n")
    response
  }

  def initialize(): Future[Unit] = {

    val credentials = AwsBasicCredentials.create("test", "test")

    val credentialsProvider = StaticCredentialsProvider.create(credentials)

    val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
      .region(Region.US_EAST_1)
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .build()
    try { createStream(streamName = "order-stream", kinesisClient = kinesisClient) } catch {
      case e: Throwable => Thread.sleep(2000)
    }
    Future(consumer.run(kinesisClient))
  }
}
