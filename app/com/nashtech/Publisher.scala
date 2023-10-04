package com.nashtech

import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest

import java.nio.charset.Charset
import scala.util.{Failure, Success, Try}

class Publisher {
  val kinesisClient: KinesisAsyncClient = KinesisAsyncClient.builder().build()

  private def publish() = {
    val data = SdkBytes.fromString("Order()", Charset.defaultCharset())

    val putRecordRequest: PutRecordRequest = PutRecordRequest
      .builder()
      .streamName("streamName")
      .data(data)
      .build()

    Try(kinesisClient.putRecord(putRecordRequest)) match {
      case Failure(exception) => sys.error(exception.getMessage)
      case Success(value) => println("Publishing successful")
    }
  }
}
