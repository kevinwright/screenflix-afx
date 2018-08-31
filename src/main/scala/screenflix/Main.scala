package screenflix

import java.nio.file.Paths
import ExportHelpers._

object Main extends App {

  /////////////////////////////////////
  // Sanity-check input param

  if (args.length == 0) {
    sys.error(".sfmovie not specified")
    sys.exit(-1)
  }
  if(!args(0).endsWith(".sfmovie")) {
    sys.error("the supplied path needs to be a .sfmovie")
    sys.exit(-1)
  }

  /////////////////////////////////////
  // Sort paths, ensure out dir exits

  val inputDirPath = Paths.get(args(0)).normalize()
  val inputDirNamePart = inputDirPath.getFileName.toString.dropRight(8) //lose the ".sfmovie"
  val outputDirPath = inputDirPath.getParent.resolve(s"$inputDirNamePart extract")
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

  val plistFilePath = inputDirPath resolve "Info.plist"
  val meta = CaptureMetadata.load(plistFilePath)

  val motionFilePath = inputDirPath resolve "Cursor"
  val motionEntryFactory = CursorEntry.Factory(meta.captureRate)
  val motionEntries = BinaryFileParser.parse(motionFilePath, motionEntryFactory, "2SRC")

  val idToImgDetailMap = CursorImageEntry.buildDetailMap(motionEntries)

  val imagesFilePath = inputDirPath resolve "CursorImages"
  val imageEntryFactory = CursorImageEntry.Factory(idToImgDetailMap)
  val imageEntries = BinaryFileParser.parse(imagesFilePath, imageEntryFactory)
  val normalisedImageEntries = imageEntries map { _.normalised }


  /////////////////////
  // export data

  imageEntries.foreach(cie => cie.saveTiff(outputDirPath resolve s"${cie.id}.tif"))
  normalisedImageEntries.foreach(cie => cie.saveTiff(outputDirPath resolve s"norm-${cie.id}.tif"))
  println("Image extracts saved")

  val afxText = adobePasteableFormat(motionEntries, meta)


  writeTextFile(outputDirPath, "summary.json", summaryJson(motionEntries, normalisedImageEntries, meta).spaces4)
  writeTextFile(outputDirPath, "keyframes.txt", afxText)

  copyToClipboard(afxText)
  println("Keyframes copied to clipboard")

}
