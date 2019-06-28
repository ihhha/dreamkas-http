scalaVersion := "2.12.8"
version := "0.0.1"

scalacOptions += "-Ypartial-unification"

lazy val akkaVersion = "2.5.23"
lazy val akkaHttpVersion = "10.1.8"
lazy val akkaSerial = "4.1.4"


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M4",
  "ch.jodersky" %% "akka-serial-core" % akkaSerial,
  "ch.jodersky" % "akka-serial-native" % akkaSerial % "runtime",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "de.heikoseeberger" %% "akka-http-play-json" % "1.27.0",
  "com.typesafe.play" %% "play-json" % "2.7.4",

  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.mockito" % "mockito-core" % "2.28.2" % Test
)

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
