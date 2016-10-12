name := "simple-vocabulary-teacher"
version := "1.0"
scalaVersion := "2.11.8"

lazy val `simple-vocabulary-teacher` = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += filters

routesGenerator := InjectedRoutesGenerator
routesImport ++= Seq("binders.PathBinders._", "binders.QueryStringBinders._")

com.typesafe.sbt.SbtScalariform.scalariformSettings