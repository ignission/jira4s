name := "jira4s"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.12.4"
)

lazy val core = (project in file("core"))
  .settings(commonSettings)

lazy val akka = (project in file("akka"))
  .settings(commonSettings)