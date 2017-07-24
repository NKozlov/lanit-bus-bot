name := "lanit-bus-bot"

version := "2.0"

scalaVersion := "2.12.2"

resolvers += Resolver.sonatypeRepo("snapshots")

enablePlugins(UniversalPlugin)

mainClass in assembly := Some("pro.nkozlov.telegram.bots.lanitbus.Bootstrap")

// we specify the name for our fat jar
assemblyJarName in assembly := s"${name.value}-${version.value}-${scalaVersion.value}.jar"

// removes all jar mappings in universal and appends the fat jar
mappings in Universal := {
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  val filtered = universalMappings filter {
    case (file, name) => !name.endsWith(".jar")
  }
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}

mappings in Universal ++= {
  ((resourceDirectory in Compile).value * "*").get.map { f =>
    f -> s"conf/${f.name}"
  }
}

libraryDependencies += "info.mukel" %% "telegrambot4s" % "3.0.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "org.apache.poi" % "poi" % "3.15"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.15"
libraryDependencies += "com.typesafe" % "config" % "1.3.1"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % Test
// dependency for run on jre
libraryDependencies += "org.scala-lang" % "scala-library" % "2.12.2"
