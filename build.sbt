import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "screenflix-afx",
    libraryDependencies ++= Seq(
      "com.twelvemonkeys.common" % "common-lang" % "3.4",
      "com.twelvemonkeys.imageio" % "imageio-core" % "3.4",
      "com.twelvemonkeys.imageio" % "imageio-metadata" % "3.4",
      "com.twelvemonkeys.imageio" % "imageio-iff" % "3.4",
      "com.twelvemonkeys.imageio" % "imageio-tiff" % "3.4",
      "com.googlecode.plist" % "dd-plist" % "1.21",
      "org.jcodec" % "jcodec" % "0.2.2",
      "org.jcodec" % "jcodec-javase" % "0.2.2",
      scalaTest % Test
    )
  )
