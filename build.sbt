name := "sample-service"

version := "0.0.1"

scalaVersion := "2.12.2"

updateOptions := updateOptions.value.withCachedResolution(true)

// old approach, use sbt-revolver instead (as global plugin)
// fork in run := true
// cancelable in Global := true

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.13.4" % "test" withSources() withJavadoc(),
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
  "com.typesafe" % "config" % "1.3.1"
)

initialCommands in console := """import loki256.github.com._
import concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

implicit val actorSystem = ActorSystem("test")
implicit val actorMaterializer = ActorMaterializer()
"""

cleanupCommands in console := """actorMaterializer.shutdown()
actorSystem.terminate()
concurrent.Await.result(actorSystem.whenTerminated, 10.seconds)
"""
