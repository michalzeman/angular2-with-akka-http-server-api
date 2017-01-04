enablePlugins(JavaAppPackaging)

organization := "io.forward"

name := "akka-http-server"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.16"
  val scalaTestV  = "2.2.6"
  val akkaHttpV   = "10.0.1"
  Seq(
    "com.typesafe"       % "config"                               % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core"                       % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream"                          % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json"                 % akkaHttpV,
    "com.google.inject"  % "guice"                                % "4.1.0",
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "ch.qos.logback"     % "logback-classic"                      % "1.1.3",
    "com.typesafe.akka" %% "akka-slf4j"                           % akkaV,
    "com.typesafe.akka" %% "akka-testkit"                         % akkaV,
    "org.mockito"       %  "mockito-all"                          % "1.10.19",
    "org.scalatest"     %% "scalatest"                            % "3.0.1" %   "test",
    "com.typesafe.akka" %% "akka-http-testkit"                    % akkaHttpV % "test",

    //DB dependencies
    "org.postgresql"    % "postgresql"                            % "9.4-1203-jdbc42",
    "com.zaxxer"        % "HikariCP"                              % "2.4.1"
  )
}
