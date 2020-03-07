name := "jira4s-akka"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"    % "10.1.11",
  "com.typesafe.akka" %% "akka-stream"  % "2.6.3",
  "org.slf4j"         %  "slf4j-api"    % "1.7.30",
  "org.scalatest"     %% "scalatest"    % "3.1.1"     % "test"
)