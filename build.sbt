import sbt.librarymanagement.ConflictWarning

enablePlugins(JavaAppPackaging)

name := "pekko-http-microservice"
organization := "com.theiterators"
version := "1.0"
scalaVersion := "3.3.0"

conflictWarning := ConflictWarning.disable

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= {
  val pekkoHttpV     = "1.0.0"
  val pekkoV         = "1.0.1"
  val circeV         = "0.14.4"
  val scalaTestV     = "3.2.16"
  val pekkoHttpJsonV = "0.0.0_1-b873253a-SNAPSHOT"
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
