val scalaV = "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "MAEDN",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaV,
    libraryDependencies ++= {
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq(
        "org.scalameta" %% "munit" % "1.0.0" % Test,
        "org.scalactic" %% "scalactic" % "3.2.10",
        "org.scalatest" %% "scalatest" % "3.2.10" % Test,
        "org.scalatestplus" %% "mockito-3-4" % "3.2.9.0" % Test,
        "org.jline" % "jline" % "3.27.1",
        "org.scalafx" %% "scalafx" % "21.0.0-R32",
        "org.scalafx" %% "scalafx-extras" % "0.10.1",
        "com.google.inject" % "guice" % "5.1.0",
        "net.codingwell" %% "scala-guice" % "7.0.0",
        "org.playframework" %% "play-json" % "3.0.4",
        "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
        "com.spotify" % "docker-client" % "8.16.0"
      ) ++ Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "23" classifier osName)
    },
    fork := true,
    // Assembly configuration
    assembly / mainClass := Some("de.htwg.se.MAEDN.App"),
    assembly / assemblyJarName := "maedn-game.jar",
    // Merge strategy for assembly
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*)  => MergeStrategy.discard
      case PathList("module-info.class")  => MergeStrategy.discard
      case "application.conf"             => MergeStrategy.concat
      case "reference.conf"               => MergeStrategy.concat
      case x if x.endsWith(".class")      => MergeStrategy.first
      case x if x.endsWith(".properties") => MergeStrategy.first
      case x if x.endsWith(".xml")        => MergeStrategy.first
      case x if x.endsWith(".txt")        => MergeStrategy.first
      case x                              => MergeStrategy.first
    }
  )
