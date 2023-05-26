import sbt.librarymanagement.ConflictWarning

enablePlugins(JavaAppPackaging)

name := "pekko-http-microservice"
organization := "com.theiterators"
version := "1.0"
scalaVersion := "3.3.0"

conflictWarning := ConflictWarning.disable

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += "Apache Snapshots" at "https://repository.apache.org/content/repositories/snapshots/"
resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= {
  val pekkoHttpV     = "0.0.0+4407-64fd8ac8-SNAPSHOT"
  val pekkoV         = "0.0.0+26572-982780b0-SNAPSHOT"
  val circeV         = "0.14.4"
  val scalaTestV     = "3.2.15"
  val pekkoHttpJsonV = "1.40.0-RC3_20-9b54213e-SNAPSHOT"
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
