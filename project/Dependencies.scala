import sbt._

object Dependencies {
  val CatsEffectVersion  = "2.3.3"
  val GatlingVersion     = "3.5.1"
  val MonixVersion       = "3.3.0"
  val LogbackVersion     = "1.2.3"
  val SttpVersion        = "3.1.6"
  val ScalaTestVersion   = "3.1.1"
  val SttpBackendVersion = "3.1.6"

  val core = Seq(
    "com.softwaremill.sttp.client3" %% "core"                            % SttpVersion,
    "io.monix"                      %% "monix"                           % MonixVersion,
    "io.monix"                      %% "monix-eval"                      % MonixVersion,
    "org.typelevel"                 %% "cats-effect"                     % CatsEffectVersion,
    "com.softwaremill.sttp.client3" %% "async-http-client-backend-monix" % SttpBackendVersion,
    "org.scalatest"                 %% "scalatest"                       % ScalaTestVersion % Test
  )
  val gatling = Seq(
    "io.gatling" % "gatling-test-framework" % GatlingVersion
  )
}
