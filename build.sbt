scalaVersion := "2.12.8"
version := "0.0.1"

scalacOptions += "-Ypartial-unification"

lazy val akkaVersion = "2.5.23"
lazy val akkaHttpVersion = "10.1.8"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M4",
  "ch.jodersky" %% "akka-serial-core" % "4.1.2",
  "ch.jodersky" % "akka-serial-native" % "4.1.2" % "runtime",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "de.heikoseeberger" %% "akka-http-play-json" % "1.26.0",
  "com.typesafe.play" %% "play-json" % "2.7.3",

  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
