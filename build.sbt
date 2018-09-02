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
      "org.spire-math" %% "jawn-parser" % "0.13.0", //for circe literals
      "io.circe" %% "circe-core" % "0.10.0-M2",
      "io.circe" %% "circe-generic" % "0.10.0-M2",
      "io.circe" %% "circe-parser" % "0.10.0-M2",
      "io.circe" %% "circe-literal" % "0.10.0-M2",
      "com.beachape" %% "enumeratum" % "1.5.13",
      "com.beachape" %% "enumeratum-circe" % "1.5.13",
      scalaTest % Test
    )
  )
