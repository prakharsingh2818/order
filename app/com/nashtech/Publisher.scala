package com.nashtech

import com.amazonaws.SDKGlobalConfiguration
import com.amazonaws.services.kinesis.AmazonKinesis
import com.nashtech.order.v1.models.Order
import com.nashtech.order.v1.models.json._
import play.api.libs.json.Json
import software.amazon.awssdk.core.{SdkBytes, SdkSystemSetting}
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.{CreateStreamRequest, CreateStreamResponse, DescribeStreamRequest, GetShardIteratorRequest, PutRecordsRequestEntry, ResourceNotFoundException, ShardIteratorType, PutRecordRequest => PutRecordRequestV2}

import java.nio.charset.Charset
import scala.Console.println
import scala.concurrent.duration.SECONDS
import scala.util.{Failure, Success, Try}

object Publisher {
  // val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder().build()

  def publish(kinesisClient: AmazonKinesis) = {
    // AmazonKinesisClient.builder().build()
    val data = SdkBytes.fromString("Hello World in Kinesis", Charset.defaultCharset())
    // val putRecordRequest: PutRecordRequest = new PutRecordRequest()
    /*putRecordRequest.setStreamName("lambda-stream")
    putRecordRequest.setData(data.asByteBuffer())
    putRecordRequest.setPartitionKey("001")*/




    Try {
      kinesisClient.putRecord("lambda-stream", data.asByteBuffer(), "001")
      // kinesisClient.putRecord(putRecordRequest)
    } match {
      case Failure(exception) => sys.error(exception.getMessage)
      case Success(_) => println(s"[${getClass.getName}] - Publishing successful")
    }
  }

  private def createStream(kinesisClient: KinesisAsyncClient, streamName: String, numAttempts: Int = 0): CreateStreamResponse = {
    println("---------------------------")
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

  def publishV2(kinesisClient: KinesisAsyncClient, order: Order): Unit = {
    System.setProperty(com.amazonaws.SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SdkSystemSetting.CBOR_ENABLED.property(), "false")
    val streamName = "order-stream-1"
    println(s"11111111111111111111111111111111")

    Try(kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build()).get(10, SECONDS)) match {
      case Failure(_: ResourceNotFoundException) =>
        println(s"22222222222222222222222222222222222222")
        createStream(kinesisClient, streamName)
      case Failure(ex) => // no-op
        createStream(kinesisClient, streamName)

        println(s"333333333333333333333333 $ex")
      case Success(value) => // no-op
        println(s"44444444444444444444444444 ${value}")
    }
    val orderJson = Json.toJson(order)
    println(s"****************************** $orderJson")
    val data = Json.stringify(orderJson).getBytes("UTF-8")

    val putRecordRequest: PutRecordRequestV2 = PutRecordRequestV2.builder()
      .streamName(streamName)
      .partitionKey("1")
      .data(SdkBytes.fromByteArray(data))
      .build()


    Try {
      kinesisClient.putRecord(putRecordRequest).get(10, SECONDS)
      println(s"1-*****************\n\n${putRecordRequest.data().asByteBuffer()}\n\n")
      println(s"2-*****************\n\n${putRecordRequest.data().asByteArray().mkString("{", ", ", "}")}\n\n")
      println(s"3-*****************\n\n${putRecordRequest.data().asUtf8String()}\n\n")

    } match {
      case Failure(exception) => throw exception
      case Success(_) => println(s"[${getClass.getName}] - Publishing successful")
    }
  }
}
