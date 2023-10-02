ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

lazy val root = (project in file("."))
  .settings(
    name := "order"
  ).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  ws,
  // logback,
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.typesafe.play" %% "play-server" % "2.8.20",
  // "com.typesafe.play" %% "play-ws" % "2.9.0-M7",
  "com.amazonaws" % "amazon-kinesis-client" % "1.14.10",
  "software.amazon.kinesis" % "amazon-kinesis-client" % "2.4.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  // "org.slf4j" % "slf4j-api" % "2.0.5",
  // "org.slf4j" % "slf4j-simple" % "2.0.5",
  // "org.slf4j" % "slf4j-nop" % "2.0.9" % Test,
  // "org.slf4j" % "slf4j-api" % "2.0.9",
  // "org.slf4j" % "slf4j-nop" % "2.0.9" % Test,
  "org.slf4j" % "slf4j-api" % "2.0.6",
  "ch.qos.logback" % "logback-classic" % "1.4.7",
  // "ch.qos.logback" % "logback-core" % "1.4.11",
  "joda-time" % "joda-time" % "2.12.5",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.14.2"
)

dependencyOverrides ++= Seq(
  "com.google.inject" % "guice" % "5.1.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0")

// Resolve scala-xml version dependency mismatch, see https://github.com/sbt/sbt/issues/7007
ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
  // "org.slf4j" % "slf4j-api" % VersionScheme.Always,
  // "org.slf4j" % "slf4j-simple" % VersionScheme.Always,
  // "ch.qos.logback" % "logback-classic" % VersionScheme.Always,
  // "ch.qos.logback" % "logback-core" % VersionScheme.Always
)

