name := "lanit-bus-bot"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "info.mukel" %% "telegrambot4s" % "2.0.2-SNAPSHOT"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "org.apache.poi" % "poi" % "3.15"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.15"
