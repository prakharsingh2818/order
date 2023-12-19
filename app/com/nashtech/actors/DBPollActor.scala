package com.nashtech.actors

import anorm.{RowParser, SQL, SqlParser, ~}
import com.nashtech.order.v1.models.Order
import org.joda.time.DateTime
import play.api.db.Database
import play.api.i18n.Lang.logger.logger

import scala.util.{Failure, Success, Try}

case class ProcessQueueOrder(
      processingQueueId: Int,
      order: Order,
      createdAt: DateTime,
      updatedAt: DateTime,
      operation: String
    )

abstract class DBPollActor(schema: String = "public", table: String) extends PollActor {

  import DBPollActor._

  def db: Database

  private val processingTable = {
    val tempName = s"${table}_processing_queue"
    if (schema.equalsIgnoreCase("public")) tempName
    else s"${schema}.${tempName}"
  }

  private val journalTable = {
    val tempName = s"${table}_journal"
    if (schema.equalsIgnoreCase("public")) tempName
    else s"${schema}.${tempName}"
  }

  startPolling()

  override def preStart(): Unit = {
    log.info("[DBPollActor] Pre-Start")
  }

  def process(record: ProcessQueueOrder): Unit

  override def processRecord(): Unit = {
    println("Inside processRecord method")
    val record = getEarliestRecord(processingTable)
    safeProcessRecord(record)

  }

  private def safeProcessRecord(record: ProcessQueueOrder): Unit = {
    Try {
      logger.info("Inside safeProcessRecord method")
      process(record)
    } match {
      case Success(_) =>
        logger.info("Continuing with safeProcessRecord method")
        deleteProcessingQueueRecord(record.processingQueueId)
        insertJournalRecord(record)

      case Failure(ex) =>
        logger.info("Discontinuing with safeProcessRecord method")
        setErrors(record.processingQueueId, ex)
    }
  }

  private def deleteProcessingQueueRecord(id: Int): Int = {
    db.withConnection { implicit connection =>
      SQL(deleteQuery(id, processingTable)).executeUpdate()
    }
  }

  private def insertJournalRecord(record: ProcessQueueOrder): Unit = {
    db.withConnection { implicit connection =>
      SQL(insertQuery(record, journalTable)).executeInsert()
    }
  }

  private def setErrors(processingQueueId: Int, throwable: Throwable): Int = {
    db.withConnection { implicit connection =>
      SQL(setErrorsQuery(processingQueueId, throwable, processingTable)).executeUpdate()
    }
  }

  private def getEarliestRecord(processingTable: String): ProcessQueueOrder = {
    db.withConnection { implicit connection =>
      SQL(baseQuery(processingTable)).as(processingQueueOrderParser().single)
    }
  }
}

object  DBPollActor {

  private def baseQuery(processingTable: String): String =
    s"""
       |select
       |processing_queue_id,
       |id,
       |number,
       |merchant_id,
       |submitted_at,
       |created_at,updated_at,
       |total,
       |operation
       |from ${processingTable}
       |order by created_at asc limit 1
       |""".stripMargin

  private def deleteQuery(id: Int, processingTable: String): String =
    s"""
       |delete from ${processingTable} where processing_queue_id = $id
       |""".stripMargin

  private def insertQuery(record: ProcessQueueOrder, journalTable: String): String =
    s"""
       |insert into $journalTable (
       |processing_queue_id,
       |id,
       |number,
       |merchant_id,
       |total,
       |submitted_at,
       |created_at,
       |updated_at, operation
       |)
       |values
       |  (
       |    '${record.processingQueueId}', '${record.order.id}',
       |    '${record.order.number}', '${record.order.merchantId}',
       |    ${record.order.total}, '${record.order.submittedAt}',
       |    '${record.createdAt}', '${record.updatedAt}',
       |    '${record.operation}'
       |  )
       |""".stripMargin

  private def setErrorsQuery(id: Int, ex: Throwable, processingTable: String): String = {
    s"""
       |update $processingTable set error_message = '${ex.getMessage}' where processing_queue_id = $id
       |""".stripMargin
  }

  private def processingQueueOrderParser(): RowParser[ProcessQueueOrder] = {
    SqlParser.int("processing_queue_id") ~
      SqlParser.str("id") ~
      SqlParser.str("number") ~
      SqlParser.str("merchant_id") ~
      SqlParser.double("total") ~
      SqlParser.get[DateTime]("submitted_at") ~
      SqlParser.get[DateTime]("created_at") ~
      SqlParser.get[DateTime]("updated_at") ~
      SqlParser.str("operation") map {

      case processingQueueId ~ id ~ number ~ merchantId ~ total ~ submittedAt ~ createdAt ~ updatedAt ~ operation =>
        ProcessQueueOrder(
          processingQueueId, Order(id, number, merchantId, submittedAt, total), createdAt, updatedAt, operation
        )
    }
  }
}