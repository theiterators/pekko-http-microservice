import sbt.librarymanagement.ConflictWarning

enablePlugins(JavaAppPackaging)

name := "pekko-http-microservice"
organization := "com.theiterators"
version := "1.0"
scalaVersion := "3.4.1"

conflictWarning := ConflictWarning.disable

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= {
  val pekkoHttpV     = "1.0.1"
  val pekkoV         = "1.0.3"
  val circeV         = "0.14.9"
  val scalaTestV     = "3.2.19"
  val akkaHttpCirceV = "1.39.2"
  val pekkoHttpJsonV = "2.6.0"

  Seq(
    "org.apache.pekko"     %% "pekko-actor"        % pekkoV,
    "org.apache.pekko"     %% "pekko-stream"       % pekkoV,
    "org.apache.pekko"     %% "pekko-http"         % pekkoHttpV,
    "org.apache.pekko"     %% "pekko-testkit"      % pekkoV % "test",
    "org.apache.pekko"     %% "pekko-http-testkit" % pekkoHttpV % "test",
    "io.circe"             %% "circe-core"         % circeV,
    "io.circe"             %% "circe-parser"       % circeV,
    "io.circe"             %% "circe-generic"      % circeV,
    "com.github.pjfanning" %% "pekko-http-circe"   % pekkoHttpJsonV,
    "org.scalatest"        %% "scalatest"          % scalaTestV % "test"
  )
}

Revolver.settings
