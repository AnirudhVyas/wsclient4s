import Dependencies._
lazy val root = (project in file("."))
  .settings(
    organization := "me.free",
    name := "wsclient4s",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.3",
    libraryDependencies ++= core,
    libraryDependencies ++= gatling,
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials")
  )
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-target:jvm-1.8",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:postfixOps",
  "-feature",
  "-Xfatal-warnings"
)
