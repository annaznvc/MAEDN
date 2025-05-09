## Project Status & CI

- [![Scala CI](https://github.com/annaznvc/MAEDN/actions/workflows/scala.yml/badge.svg)](https://github.com/annaznvc/MAEDN/actions/workflows/scala.yml)
- [![Last Commit](https://img.shields.io/github/last-commit/annaznvc/MAEDN.svg?color=blue)](https://github.com/annaznvc/MAEDN/commits/main)
- [![Build Status](https://travis-ci.org/annaznvc/MAEDN.svg?branch=main)](https://travis-ci.org/annaznvc/MAEDN)

## Coverage

- [![Coverage Status](https://coveralls.io/repos/github/annaznvc/MAEDN/badge.svg?branch=main)](https://coveralls.io/github/annaznvc/MAEDN?branch=main)

### Sunburst

[![Sunburst](https://codecov.io/gh/annaznvc/MAEDN/graphs/sunburst.svg?token=1RD2DIMUZK)](https://codecov.io/gh/annaznvc/MAEDN)

Die Sunburst-Darstellung zeigt die Codeabdeckung dieses Projekts in einem kreisförmigen Diagramm, bei dem jedes Verzeichnis und jede Datei als einzelner Sektor dargestellt ist. Die Farbgebung – grün, gelb und rot – gibt den Abdeckungsgrad an: Grün steht für hohe, Rot für geringe Testabdeckung. So lässt sich auf einen Blick erkennen, welche Teile des Codes gut getestet sind und wo noch Optimierungsbedarf besteht.


# Mensch ärgere dich nicht 🎲 – Scala Edition

Ein digitaler Nachbau des klassischen deutschen Brettspiels **„Mensch ärgere dich nicht“**, entwickelt in **Scala**. Ziel des Spiels ist es, alle eigenen Spielfiguren sicher ins Ziel zu bringen – bevor die Mitspieler sie rauswerfen!

## 📌 Features

- 👥 2–4 Spieler
- ♟️ Spiellogik nach den bekannten Regeln
- 🖥️ Textbasiertes Benutzerinterface (Konsole)

## 🛠️ Installation & Ausführen

### Voraussetzungen

- [Scala 2.13+](https://www.scala-lang.org/)
- [sbt (Scala Build Tool)](https://www.scala-sbt.org/)

### Projekt klonen und starten

```bash
git clone https://github.com/annaznvc/MAEDN.git
cd MAEDN
sbt run
