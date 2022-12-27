name := "primenumbers-grpc"
scalaVersion := "2.13.4"
version := "1.0.0"

lazy val akkaVersion     = "2.7.0"
lazy val akkaHttpVersion = "10.4.0"
lazy val akkaGrpcVersion = "2.2.1"
lazy val circeVersion    = "0.14.3"

lazy val root = (project in file("."))
  .aggregate(http, grpc)

lazy val circe = Seq(
  "io.circe" %% "circe-core"       % circeVersion,
  "io.circe" %% "circe-generic"    % circeVersion,
  "io.circe" %% "circe-jackson210" % "0.14.0",
  "io.circe" %% "circe-parser"     % circeVersion
)

val ds = Seq(
  "com.typesafe.akka"          %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http2-support"       % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka"          %% "akka-stream"              % akkaVersion,
  "com.typesafe.akka"          %% "akka-discovery"           % akkaVersion,
  "com.typesafe.akka"          %% "akka-pki"                 % akkaVersion,
  // The Akka HTTP overwrites are required because Akka-gRPC depends on 10.1.x
  "com.typesafe.akka"          %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka"          %% "akka-http2-support"       % akkaHttpVersion,
  "de.heikoseeberger"          %% "akka-http-circe"          % "1.39.2",
  "ch.qos.logback"              % "logback-classic"          % "1.4.5",
  "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.5",
  "com.typesafe.akka"          %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka"          %% "akka-stream-testkit"      % akkaVersion % Test,
  "org.scalatest"              %% "scalatest"                % "3.1.1"     % Test
)

// Http front end that calls out to a gRPC back end
lazy val http = (project in file("http"))
  .enablePlugins(AkkaGrpcPlugin, AssemblyPlugin)
  .settings(libraryDependencies ++= ds ++ circe)
  .settings(assemblySettings)
  .settings(scalaVersion := "2.13.4")

lazy val grpc = (project in file("grpc"))
  .enablePlugins(AkkaGrpcPlugin, AssemblyPlugin)
  .settings(libraryDependencies ++= ds)
  .settings(assemblySettings)
  .settings(scalaVersion := "2.13.4")

lazy val assemblySettings = Seq(
  assembly / assemblyMergeStrategy := {
    case PathList("reference.conf")                       => MergeStrategy.concat
    case "META-INF/services/io.grpc.LoadBalancerProvider" => MergeStrategy.first
    case PathList("META-INF", xs @ _*)                    => MergeStrategy.discard
    case _                                                => MergeStrategy.first
  }
)
