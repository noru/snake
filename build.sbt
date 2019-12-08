enablePlugins(ScalaJSPlugin)

name := "Scala.js Snake"
scalaVersion := "2.12.10"

scalaJSUseMainModuleInitializer := true

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"
libraryDependencies += "com.thoughtworks.binding" %%% "dom" % "latest.release"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

artifactPath in(Compile, fullOptJS) := baseDirectory.value / "dist" / ((moduleName in fullOptJS).value + "-opt.js")
