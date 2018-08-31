package screenflix

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.{BufferedWriter, FileWriter}
import java.nio.file.Path

import io.circe._
import io.circe.literal._

object ExportHelpers {
  def adobePasteableFormat(motionEntries: Seq[CursorEntry], meta: CaptureMetadata): String = {
    val afxKeyframes = motionEntries.map{
      // subtract y-pos from height, screenflick uses bottom left as the origin, AFX uses top left
      entry => s"~${entry.timestamp.inFrames}~${entry.hotspotAbsolute.x}~${meta.height - entry.hotspotAbsolute.y}~0~"
    }.mkString("\n")

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
  }

  def smpteJson(smpte: Smpte): Json =
    json"""{
        "smpte": ${smpte.toString},
        "seconds": ${smpte.inSeconds},
        "frames": ${smpte.inFrames}
    }"""

  def summaryJson(
    motionEntries: Seq[CursorEntry],
    imageEntries: Seq[CursorImageEntry],
    meta: CaptureMetadata
  ): Json = {
    val keyframeSnippets = motionEntries map { entry =>
      val when = smpteJson(entry.timestamp)
      val x = entry.hotspotAbsolute.x
      val y = meta.height - entry.hotspotAbsolute.y //co-ord flip
      val imageId = entry.imageId getOrElse -1L
      val scale = entry.scale
      json"""{
          "when": $when,
          "x": $x,
          "y": $y,
          "imageId": $imageId,
          "scale": $scale
      }"""
    }

    val imageSnippets = imageEntries map { entry =>
      val id = entry.id
      val filename = s"norm-$id.tif"
      val recordedWidth = entry.recordedSize.x
      val recordedHeight = entry.recordedSize.y
      val imgHash = entry.imageHash
      json"""{
          "id": $id,
          "filename": $filename,
          "recordedWidth": $recordedWidth,
          "recordedHeight": $recordedHeight,
          "hash": $imgHash
      }"""
    }

    json""" {
        "meta": {
            "width": ${meta.width},
            "height": ${meta.height},
            "frameRate": ${meta.captureRate},
            "duration": ${smpteJson(meta.duration)}
        },
        "images": $imageSnippets,
        "keyframes": $keyframeSnippets
    }"""
  }

//  def mkJsxImporter()

  def copyToClipboard(txt: String): Unit = {
    val clipboard = Toolkit.getDefaultToolkit.getSystemClipboard
    clipboard.setContents(new StringSelection(txt), null)
  }

  def writeTextFile(outputDirPath: Path, name: String, block: String): Path =
    writeTextFile(outputDirPath, name, block.split("\n"))

  def writeTextFile(outputDirPath: Path, name: String, lines: Seq[String]): Path = {
    val path = outputDirPath.resolve(name)
    val bw = new BufferedWriter(new FileWriter(path.toFile))
    lines foreach { line =>
      bw.write(line)
      bw.newLine()
    }
    bw.close()
    println(s"$name written")
    path
  }
}
