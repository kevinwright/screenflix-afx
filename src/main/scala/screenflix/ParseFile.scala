package screenflix

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File

import org.jcodec.api.SequenceEncoder
import org.jcodec.api.awt.AWTSequenceEncoder
import org.jcodec.common.{Codec, Format}
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.model.Rational
import org.jcodec.scale.AWTUtil

object ParseFile extends App {

  val sfroot = "/Users/kevin/Library/Application Support/com.araeliumgroup.screenflick/Movies"
  val movieDir = sfroot + "/Plugin Better Run-Thru.sfmovie"

  val plistFileName = movieDir + "/Info.plist"

  val meta = CaptureMetadata.load(plistFileName)

  val cursorFileName = movieDir + "/Cursor"
  val cursorEntryFactory = CursorEntry.Factory(meta.captureRate)
  val cursorEntries = BinaryFileParser.parse(cursorFileName, cursorEntryFactory.getFromBuffer, "2SRC")

  val cursorImagesFileName = movieDir + "/CursorImages"
  val cursorImageEntries = BinaryFileParser.parse(cursorImagesFileName, CursorImageEntry.getFromBuffer)

  val afxKeyframes = cursorEntries.map{
    // subtract y-pos from height, screenflick uses bottom left as the origin, AFX uses top left
    entry => s"~${entry.timestamp.toFrame}~${entry.hotspotAbsolute.x}~${meta.height - entry.hotspotAbsolute.y}~0~"
  }.mkString("\n")

  case class ImgDetail(hotspot: IntVect, size: IntVect, scale: Double)

  val idToImgDetailSeq = for {
    entry <- cursorEntries if entry.imageId.isDefined
    imageId <- entry.imageId
  } yield imageId -> ImgDetail(entry.hotspotRelative, entry.size, entry.scale)

  val idToImgDetailMap = idToImgDetailSeq.toMap
  idToImgDetailMap.foreach(println)
  val normalisedImageEntries = cursorImageEntries map { img =>
    val detail = idToImgDetailMap(img.id)
    img.normalised(detail.size, detail.hotspot)
  }


  val afxText =
    s"""|Adobe After Effects 8.0 Keyframe Data
        |
        |~Units Per Second~${meta.captureRate}
        |~Source Width~${meta.width}
        |~Source Height~${meta.height}
        |~Source Pixel Aspect Ratio~1
        |~Comp Pixel Aspect Ratio~1
        |
        |Transform~Position
        |~Frame~X pixels~Y pixels~Z pixels
        |${afxKeyframes}
        |
        |
        |End of Keyframe Data""".stripMargin.replaceAllLiterally("~", "\t")



  val stringSelection = new StringSelection(afxText)
  val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
  clipboard.setContents(stringSelection, null)
  println("copied to clipboard")

  cursorImageEntries.foreach(cie => cie.saveTiff(s"$sfroot/${cie.id}.tif"))
  normalisedImageEntries.foreach(cie => cie.saveTiff(s"$sfroot/${cie.id}-norm.tif"))


//  val movFile = new File(s"$movieDir/pointerframes.mp4")
//  val enc = new SequenceEncoder(
//    NIOUtils.writableChannel(movFile),
//    Rational.R(1, 1),
//    Format.MOV,
//    Codec.PRORES,
//    null
//  )
//  normalisedImageEntries foreach { cie =>
//    enc.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(cie.image))
//  }
//  enc.finish()
}
