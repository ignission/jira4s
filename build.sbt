name := "jira4s"

sonatypeProfileName := "tech.ignission"

lazy val commonSettings = Seq(
  version := "0.3.0-SNAPSHOT",
  scalaVersion := "2.13.1",
  organization := "tech.ignission"
)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in publish := true
)

isSnapshot := version.value endsWith "SNAPSHOT"

lazy val publishPackages = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    val realm = "Sonatype Nexus Repository Manager"
    val staging = "https://oss.sonatype.org/content/repositories/snapshots"
    val release = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    if (isSnapshot.value)
      Some(realm at staging)
    else
      Some(realm at release)
  },
//  publishConfiguration := publishConfiguration.value.withOverwrite(true),
//  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"),
  homepage := Some(url("https://github.com/ignission")),
  licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
  scmInfo := Some(ScmInfo(url("https://github.com/ignission/jira4s"), "scm:git:git@github.com/ignission/jira4s.git")),
  developers := List(
    Developer(
      id    = "shomatan",
      name  = "Shoma Nishitateno",
      email = "shoma416@gmail.com",
      url   = url("https://github.com/shomatan")
    )
  )
)

lazy val core = (project in file("jira4s-core"))
  .settings(commonSettings)
  .settings(
    name := "jira4s-core",
    publishPackages
  )

lazy val akka = (project in file("jira4s-akka"))
  .settings(commonSettings)
  .settings(
    name := "jira4s-akka",
    publishPackages
  )
  .dependsOn(core)

lazy val test = (project in file("test"))
  .settings(commonSettings)
  .dependsOn(core, akka)

lazy val jira4s = (project in file("."))
  .settings(
    moduleName := "root",
    noPublishSettings
  )
  .aggregate(core, akka)