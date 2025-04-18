
val circe_version = "0.14.12"

val imageio_version = "3.12.0"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "3.6.4",
      version      := "0.1.0-SNAPSHOT"
    )),
    scalacOptions += "-Yretain-trees",
    name := "screenflix-afx",
    libraryDependencies ++= Seq(
      "com.twelvemonkeys.common" % "common-lang" % imageio_version,
      "com.twelvemonkeys.imageio" % "imageio-core" % imageio_version,
      "com.twelvemonkeys.imageio" % "imageio-metadata" % imageio_version,
      "com.twelvemonkeys.imageio" % "imageio-iff" % imageio_version,
      "com.twelvemonkeys.imageio" % "imageio-tiff" % imageio_version,
      "com.googlecode.plist" % "dd-plist" % "1.28",
      "io.circe" %% "circe-core" % circe_version,
      "io.circe" %% "circe-generic" % circe_version,
      "io.circe" %% "circe-parser" % circe_version,
      "io.circe" %% "circe-literal" % circe_version,
      "com.beachape" %% "enumeratum" % "1.7.6",
      "com.beachape" %% "enumeratum-circe" % "1.7.5",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    )
  )
