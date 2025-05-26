// build.sbt

import sbt._
import sbt.Keys._

val scalaV = "3.5.1"
val scalafxVersion = "22.0.0-R33"

lazy val root = (project in file("."))
  .settings(
    name := "MAEDN",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scalaV,

    // Test-Bibliotheken
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0" % Test,
      "org.scalactic" %% "scalactic" % "3.2.14",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.jline" % "jline" % "3.29.0",

      // ScalaFX-Core für Scala 3
      "org.scalafx" % "scalafx_3" % scalafxVersion
    ),

    // Forken, damit JavaFX-Optionen greifen
    fork := true,
    Compile / run / javaOptions ++= {
      // Pfad zum JavaFX-SDK lib-Verzeichnis
      val javafxLib = "C:/Users/annaz/Desktop/javafx-sdk-24.0.1/lib"

      Seq(
        "--module-path",
        javafxLib,
        "--add-modules",
        "javafx.controls,javafx.fxml",
        "--add-exports",
        "javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED",
        "--add-exports",
        "javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-exports",
        "javafx.controls/com.sun.javafx.scene.control.inputmap=ALL-UNNAMED",
        "--add-exports",
        "javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
        "--add-exports",
        "javafx.base/com.sun.javafx.event=ALL-UNNAMED",
        "--add-exports",
        "javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED"
      )
    }
  )
