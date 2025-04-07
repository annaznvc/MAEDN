val scala3Version = "3.3.1" 

lazy val root = project
  .in(file("."))
  .settings(
<<<<<<< HEAD
<<<<<<< HEAD
    name := "MAEDN",
=======
    name := "M DN",
>>>>>>> 0958b78cdb4c16440efa67c47e73c3f7dd0ab913
=======
    name := "MAEDN",
>>>>>>> 0ac73132a789c8d55234bdad6bbf51f76aec9c08
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    coverageEnabled := true,

<<<<<<< HEAD
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit"     % "1.0.0"  % Test,
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test
    )
=======
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test
>>>>>>> 0958b78cdb4c16440efa67c47e73c3f7dd0ab913
  )
