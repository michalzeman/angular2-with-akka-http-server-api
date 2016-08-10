enablePlugins(JavaAppPackaging)

organization := "io.forward"

name := "akka-http-server"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV       = "2.4.8"
  val scalaTestV  = "2.2.6"
  Seq(
    "com.typesafe"       % "config"                               % "1.3.0",
    "com.typesafe.akka" %% "akka-http-core"                       % akkaV,
    "com.typesafe.akka" %% "akka-stream"                          % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % "2.4.2-RC3",
    "com.google.inject"  % "guice"                                % "4.1.0",
    "org.scalatest"     %% "scalatest"                            % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "ch.qos.logback"     % "logback-classic"                      % "1.1.3",
    "com.typesafe.akka" %% "akka-slf4j"                           % akkaV,
    "com.typesafe.akka" %% "akka-testkit"                         % akkaV,
    "org.mockito"       %  "mockito-all"                          % "1.10.19",
    "org.scalatest"     %% "scalatest"                            % "2.1.6" % "test",
    //DB dependencies
    "org.postgresql"    % "postgresql"                            % "9.4-1203-jdbc42",
    "com.zaxxer"        % "HikariCP"                              % "2.4.1"
  )
}
