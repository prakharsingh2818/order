package com.nashtech.actors

import play.api.libs.ws.WSClient
import play.libs.ws.WSClient

import javax.inject.{Inject, Singleton}

@Singleton
class OrderJournalActor @Inject() (ws: WSClient)
extends DBPollActor(table = "orders") {
  override def process(record: ProcessQueueOrder): Unit = {
    record.operation match {
      case "Insert" | "Update" => ??? // TODO: Publish using kinesis
      case "Delete" => ???
    }
  }
}
