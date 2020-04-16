name := "jira4s-core"

val monixVersion = "3.1.0"

libraryDependencies ++= Seq(
  "io.spray"      %% "spray-json"     % "1.3.5",
  "org.typelevel" %% "cats-core"      % "2.1.1",
  "io.monix"      %% "monix"          % monixVersion,
  "io.monix"      %% "monix-eval"     % monixVersion,
  "io.monix"      %% "monix-reactive" % monixVersion,
  "org.scalatest" %% "scalatest"      % "3.1.1"       % "test"
)