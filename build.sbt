import com.fasterxml.jackson.annotation.JsonTypeInfo.As
val scala3Version = "3.5.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "MAEDN",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    coverageEnabled := true,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.jline" % "jline" % "3.29.0"
    )
  )
enablePlugins(ScoverageSbtPlugin, CoverallsPlugin)
