val scalaVersionUsed = "2.13.13"

lazy val root = project
  .in(file("."))
  .settings(
    name := "MAEDN",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaVersionUsed,
    coverageEnabled := true,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.jline" % "jline" % "3.29.0"
    )
  )

enablePlugins(ScoverageSbtPlugin, CoverallsPlugin)
