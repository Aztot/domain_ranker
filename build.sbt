ThisBuild / version := "0.2.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "domain_ranker"
  )

enablePlugins(
  JavaAppPackaging,
  DockerPlugin
)

Compile / mainClass := Some("com.ranked.domain.RankedDomainServer")
Docker / packageName := "jyavros/ranked-domain"
dockerExposedPorts ++= Seq(10202)

val AkkaVersion = "2.7.0"
val AkkaHttpVersion = "10.5.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
)
libraryDependencies += "io.spray" %% "spray-json" % "1.3.6"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "io.lemonlabs" %% "scala-uri" % "4.0.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.10"

resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Besu repository" at	"https://hyperledger.jfrog.io/artifactory/besu-maven/"
)