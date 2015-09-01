name := """ssvc"""

version := "1.0"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-optimise", "-feature", "-unchecked", "-deprecation", "-Xlint", "-Yinline-warnings")

libraryDependencies += "org.rogach" %% "scallop" % "0.9.5"

fork in (run) := true

artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}
