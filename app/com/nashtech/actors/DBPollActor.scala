package com.nashtech.actors

import anorm.{RowParser, SQL, SqlParser, ~}
import org.joda.time.DateTime
import play.api.db.Database

import scala.util.{Failure, Success, Try}

abstract class DBPollActor(schema: String = "public", table: String) extends PollActor {

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

  def process(record: ProcessQueueOrder): Try[Unit]

  override def processRecord(): Unit = {
     log.info("Inside processRecord method")
    val record = getEarliestRecord(processingTable)
    safeProcessRecord(record)

  }

  private def safeProcessRecord(record: ProcessQueueOrder) = {
    Try {
      log.info("Inside safeProcessRecord method")
      process(record)
    } match {
      case Success(_) =>
        log.info("Continuing with safeProcessRecord method")
        deleteProcessingQueueRecord(record.processingQueueId)
        insertJournalRecord(record)

      case Failure(ex) =>
        log.info("Discontinuing with safeProcessRecord method")
        setErrors(record.processingQueueId, ex)
    }
  }

  private def deleteProcessingQueueRecord(id: Int) = {
    db.withConnection { implicit connection =>
      SQL(deleteQuery(id)).executeUpdate()
    }
  }

  private def insertJournalRecord(record: ProcessQueueOrder) = {
    db.withConnection { implicit connection =>
      SQL(insertQuery(record)).executeInsert()
    }
  }

  private def setErrors(processingQueueId: Int, throwable: Throwable) = {
    db.withConnection { implicit connection =>
      SQL(setErrorsQuery(processingQueueId, throwable)).executeUpdate()
    }
  }

  private def getEarliestRecord(processingTable: String): ProcessQueueOrder = {
    db.withConnection { implicit connection =>
      SQL(baseQuery).as(processingQueueOrderParser().single)
    }
  }

  private def baseQuery =
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

  private def deleteQuery(id: Int) =
    s"""
       |delete from ${processingTable} where processing_queue_id = $id
       |""".stripMargin

  private def insertQuery(record: ProcessQueueOrder) =
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
       |    '${record.processingQueueId}', '${record.id}',
       |    '${record.number}', '${record.merchantId}',
       |    ${record.total}, '${record.submittedAt}',
       |    '${record.createdAt}', '${record.updatedAt}',
       |    '${record.operation}'
       |  )
       |""".stripMargin

  private def setErrorsQuery(id: Int, ex: Throwable) = {
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
          processingQueueId, id, number, merchantId, total, submittedAt, createdAt, updatedAt, operation
        )
    }
  }
}

case class ProcessQueueOrder(
  processingQueueId: Int,
  id: String,
  number: String,
  merchantId: String,
  total: Double,
  submittedAt: DateTime,
  createdAt: DateTime,
  updatedAt: DateTime,
  operation: String
)