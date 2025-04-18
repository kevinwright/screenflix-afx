package screenflix

import java.nio.file.Paths
import ExportHelpers.*

import java.util.zip.ZipFile
import scala.jdk.CollectionConverters.*

@main def main(args: String*): Unit = {

  /////////////////////////////////////
  // Sanity-check input param

  if (args.isEmpty) {
    sys.error(".sfmovie not specified")
    sys.exit(-1)
  }

  val inputPathStr = args(0)

  if(!inputPathStr.endsWith(".sfmovie")) {
    sys.error("the supplied path needs to be a .sfmovie")
    sys.exit(-1)
  }

  /////////////////////////////////////
  // Sort paths, ensure out dir exits

  val inputPath = Paths.get(inputPathStr).normalize()
  val inputPathNamePart = inputPath.getFileName.toString.dropRight(8) //lose the ".sfmovie"
  val outputDirPath = inputPath.getParent.resolve(s"$inputPathNamePart extract")
  val outputDirFile = outputDirPath.toFile

  if(outputDirFile.exists()) {
    if(outputDirFile.isFile) sys.error(s"$outputDirPath already exists and is not a directory")
    else println(s"Using existing output dir: $outputDirFile")
  } else {
    println(s"Creating output dir: $outputDirFile")
    outputDirFile.mkdir()
  }

  /////////////////////////////////////
  // Load data

  val plistFilePath = inputPath.resolve("Info.plist")
  val meta = CaptureMetadata.load(plistFilePath)

  val motionFilePath = inputPath.resolve("Cursor")
  val motionEntryFactory = CursorEntry.Factory(meta.captureRate)
  val motionEntries = BinaryFileParser.parse(motionFilePath, motionEntryFactory, "2SRC")

  val idToImgDetailMap = CursorImageEntry.buildDetailMap(motionEntries)

  val imagesFilePath = inputPath.resolve("CursorImages")
  val imageEntryFactory = CursorImageEntry.Factory(idToImgDetailMap)
  val imageEntries = BinaryFileParser.parse(imagesFilePath, imageEntryFactory)
  val normalisedImageEntries = imageEntries map { _.normalised }

  val eventsFilePath = inputPath.resolve("Events")
  val eventFactory = Event.Factory(meta.captureRate)
  val events = BinaryFileParser.parse(eventsFilePath, eventFactory, "SCISdata")

  events foreach { println }


  /////////////////////
  // export data

  imageEntries.foreach(cie => cie.saveTiff(outputDirPath.resolve(s"${cie.id}.tif")))
  normalisedImageEntries.foreach(cie => cie.saveTiff(outputDirPath.resolve(s"norm-${cie.id}.tif")))
  println("Image extracts saved")

  val afxText = adobePasteableFormat(motionEntries, meta)


  writeTextFile(outputDirPath, "summary.json", summaryJson(motionEntries, normalisedImageEntries, events, meta).spaces4)
  writeTextFile(outputDirPath, "keyframes.txt", afxText)

  copyToClipboard(afxText)
  println("Keyframes copied to clipboard")

}
