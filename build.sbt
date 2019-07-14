crossScalaVersions in ThisBuild := List("2.13.0", "2.12.8")
scalaVersion in ThisBuild := crossScalaVersions.value.head

version := "0.0.10"

maintainer := "NZonov"

lazy val akkaVersion = "2.5.23"
lazy val akkaHttpVersion = "10.1.8"
lazy val akkaSerial = "4.1.4"


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.0.0-M4",
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.27.0",
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "com.fazecast" % "jSerialComm" % "[2.0.0,3.0.0)",

  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.23" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.mockito" % "mockito-core" % "2.28.2" % Test
)

enablePlugins(JavaAppPackaging)
enablePlugins(UniversalPlugin)

mappings in Universal += {
  val conf = (resourceDirectory in Compile).value / "application.conf"
  conf -> "conf/application.conf"
}

mappings in Universal += {
  val conf = (resourceDirectory in Compile).value / "logback.xml"
  conf -> "conf/logback.xml"
}
