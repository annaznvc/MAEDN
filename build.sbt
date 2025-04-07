val scala3Version = "3.3.1" 

lazy val root = project
  .in(file("."))
  .settings(
    name := "MAEDN",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    coverageEnabled := true,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit"     % "1.0.0"  % Test,
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test
    )
  )
