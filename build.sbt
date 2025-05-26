val scala3Version = "3.5.1"
val javafxVersion = "24.0.1"

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
      "org.jline" % "jline" % "3.29.0",

      // JavaFX core
      "org.openjfx" % "javafx-base" % javafxVersion classifier "win",
      "org.openjfx" % "javafx-controls" % javafxVersion classifier "win",
      "org.openjfx" % "javafx-fxml" % javafxVersion classifier "win",
      "org.openjfx" % "javafx-graphics" % javafxVersion classifier "win"
    ),
    Compile / run / javaOptions ++= Seq(
      "--module-path",
      "C:/Users/annaz/Desktop/javafx-sdk-24.0.1/lib",
      "--add-modules",
      "javafx.controls,javafx.fxml"
    )
  )
