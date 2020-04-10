name := "spark-single-project"
version := "0.1"
scalaVersion := "2.11.12"
val sparkVersion = "2.4.5"

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    batchjobsaverager,
    streamingaverager,
    streamingpredictor
  )

lazy val common = project
  .settings(
    name := "common",
    settings,
    libraryDependencies ++= commonDependencies
  )

lazy val batchjobsaverager = project
  .settings(
    name := "batchjobsaverager",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val streamingaverager = project
  .settings(
    name := "streamingaverager",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )

lazy val streamingpredictor = project
  .settings(
    name := "streamingpredictor",
    settings,
    assemblySettings,
    libraryDependencies ++= commonDependencies
  )
  .dependsOn(
    common
  )


lazy val settings = Seq(
  scalaVersion := "2.11.12",
  resolvers += "bintray-spark-packages" at
    "https://dl.bintray.com/spark-packages/maven/",
  resolvers += "Typesafe Simple Repository" at
  "http://repo.typesafe.com/typesafe/simple/maven-releases/",
  resolvers += "MavenRepository" at
    "https://mvnrepository.com/",
  resolvers += "DataStax Repo" at "https://repo.datastax.com/public-repos/",
  resolvers += Resolver.url("sbt-plugin-releases-scalasbt", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))
)

lazy val commonDependencies = Seq(
  // spark core
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided",

  // spark-modules
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion % "provided",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.4.3" % "provided",

  // Kafka
  "org.apache.kafka" %% "kafka" % "2.4.0" % "provided",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.4.0" % "provided"
)


lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)