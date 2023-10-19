import com.typesafe.config.ConfigFactory

import java.sql.{Connection, DriverManager, Statement}

object Connection {
  private val config = ConfigFactory.load()
  private val url = config.getString("postgres.url")
  private val userName = config.getString("postgres.username")
  private val password = config.getString("postgres.password")

  val connection: Connection = DriverManager.getConnection(url, userName, password)


  val statement: Statement = connection.createStatement()

}