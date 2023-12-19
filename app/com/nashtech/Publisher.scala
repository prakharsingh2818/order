package com.nashtech

import com.amazonaws.SDKGlobalConfiguration
import com.amazonaws.services.kinesis.AmazonKinesis
import com.typesafe.scalalogging.LazyLogging
import play.api.i18n.Lang.logger
import com.nashtech.order.v1.models.Order
import com.nashtech.order.v1.models.json._

import play.api.libs.json.Json
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.{SdkBytes, SdkSystemSetting}
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.{CreateStreamRequest, CreateStreamResponse, DescribeStreamRequest, GetShardIteratorRequest, PutRecordsRequestEntry, ResourceNotFoundException, ShardIteratorType, PutRecordRequest => PutRecordRequestV2}

import java.nio.charset.Charset
import scala.Console.println
import scala.concurrent.duration.SECONDS
import scala.util.{Failure, Success, Try}

object Publisher {

  private def createStream(kinesisClient: KinesisAsyncClient, streamName: String, numAttempts: Int = 0): CreateStreamResponse = {
    logger.info(s"Creating a new stream -> $streamName.")

    val response = kinesisClient.createStream(
      CreateStreamRequest.builder()
        .streamName(streamName)
        .shardCount(1)
        .build())
      .get(10, SECONDS)

    val stream = kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build())
    val streamNameDesc = stream.get.streamDescription().streamName()
    System.setProperty("aws.cborEnabled", "false")
    val shardId = stream.get().streamDescription().shards().get(0).shardId()

    val getShardIteratorRequest = GetShardIteratorRequest.builder()
      .streamName(streamNameDesc)
      .shardId(shardId)
      .shardIteratorType(ShardIteratorType.LATEST)
      .build()

    val shardIterator = kinesisClient.getShardIterator(getShardIteratorRequest).get().getValueForField("ShardIterator", classOf[String])
    logger.info(s"getting shardIterator $shardIterator")
    response
  }

  def publish(order: Order): Unit = {
    val credentials = AwsBasicCredentials.create("test", "test")

    val credentialsProvider = StaticCredentialsProvider.create(credentials)

    val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder()
      .region(Region.US_EAST_1)
      .credentialsProvider(credentialsProvider)
      .endpointOverride(new java.net.URI("http://localhost:4566"))
      .httpClient(NettyNioAsyncHttpClient.builder().build())
      .build()

    System.setProperty(com.amazonaws.SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SdkSystemSetting.CBOR_ENABLED.property(), "false")

    val streamName = "order-stream"

    Try(kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build()).get(10, SECONDS)) match {
      case Failure(_: ResourceNotFoundException) =>
        createStream(kinesisClient, streamName)
      case Failure(_: ResourceInUseException) => Thread.sleep(3000)
      case Failure(_) => createStream(kinesisClient, streamName)
      case Success(value) => // no-op
        logger.info(s"getting described stream $value")
    }

    val orderJson = Json.toJson(order)
    val data = Json.stringify(orderJson).getBytes("UTF-8")

    val putRecordRequest: PutRecordRequestV2 = PutRecordRequestV2.builder()
      .streamName(streamName)
      .partitionKey("1")
      .data(SdkBytes.fromByteArray(data))
      .build()

    Try {
      kinesisClient.putRecord(putRecordRequest).get(10, SECONDS)
      logger.info(s"putting records into the stream")

    } match {
      case Failure(exception) => throw exception
      case Success(_) => logger.info(s"[${getClass.getName}] - Publishing successful")
    }
  }
}
