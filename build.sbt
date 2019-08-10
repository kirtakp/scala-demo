name := "scala-demo"

version := "0.1"

scalaVersion := "2.12.0"

val mongodbDriverVersion = "2.6.0"
val catsEffectVersion = "1.3.0"
val http4sVersion = "0.21.0-M3"
val ioCircleVersion = "0.12.0-M4"
val slf4jVersion = "1.7.26"
val log4jVersion = "1.2.17"

libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % mongodbDriverVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion withSources() withJavadoc(),
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "io.circe" %% "circe-generic" % ioCircleVersion,
  "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
  "log4j" % "log4j" % log4jVersion)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")