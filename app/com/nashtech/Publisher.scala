package com.nashtech

import com.amazonaws.SDKGlobalConfiguration
import com.amazonaws.services.kinesis.AmazonKinesis
import software.amazon.awssdk.core.{SdkBytes, SdkSystemSetting}
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException
import software.amazon.awssdk.services.kinesis.KinesisClient
import software.amazon.awssdk.services.kinesis.model.{CreateStreamRequest, CreateStreamResponse, DescribeStreamRequest, PutRecordsRequestEntry, PutRecordRequest => PutRecordRequestV2}

import java.nio.charset.Charset
import scala.Console.println
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

  private def createStream(kinesisClient: KinesisClient, streamName: String, numAttempts: Int = 0): CreateStreamResponse = {
    println("---------------------------")
    kinesisClient.createStream(
      CreateStreamRequest.builder()
        .streamName(streamName)
        .shardCount(1)
        .build())
  }

  def publishV2(kinesisClient: KinesisClient): Unit = {
    System.setProperty(com.amazonaws.SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true")
    System.setProperty(SdkSystemSetting.CBOR_ENABLED.property(), "false")
    val streamName = "order-stream"
    println(s"11111111111111111111111111111111")
    Try(kinesisClient.describeStream(DescribeStreamRequest.builder().streamName(streamName).build())) match {
      case Failure(_: ResourceNotFoundException) =>
        println(s"22222222222222222222222222222222222222")
        createStream(kinesisClient, streamName)
      case Failure(ex) => // no-op
        // createStream(kinesisClient, streamName)

        println(s"333333333333333333333333")
      case Success(_) => // no-op
        println(s"44444444444444444444444444")
    }

    val data = "Hello World in Kinesis"

    val putRecordRequest: PutRecordRequestV2 = PutRecordRequestV2.builder()
      .streamName(streamName)
      .partitionKey("1")
      .data(SdkBytes.fromString(data, Charset.defaultCharset()))
      .build()


    Try {
      kinesisClient.putRecord(putRecordRequest)
      println(s"\n\n${putRecordRequest.data().asByteBuffer()}\n\n")
      println(s"\n\n${putRecordRequest.data().asByteArray().mkString("{", ", ", "}")}\n\n")
      println(s"\n\n${putRecordRequest.data().asUtf8String()}\n\n")

    } match {
      case Failure(exception) => throw exception
      case Success(_) => println(s"[${getClass.getName}] - Publishing successful")
    }
  }
}
