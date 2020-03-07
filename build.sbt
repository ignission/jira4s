name := "jira4s"

lazy val commonSettings = Seq(
  version := "0.1.0",
  scalaVersion := "2.13.1"
)

lazy val core = (project in file("core"))
  .settings(commonSettings)

lazy val akka = (project in file("akka"))
  .settings(commonSettings)
  .dependsOn(core)

lazy val test = (project in file("test"))
  .settings(commonSettings)
  .dependsOn(core, akka)