package screenflix

import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import java.nio.ByteBuffer
import java.io.ByteArrayInputStream
import java.nio.file.Path

case class CursorImageEntry(id: Int, image: BufferedImage, imageHash: Long, hotspot: IntVect, recordedSize: IntVect) {
  def normalised: CursorImageEntry = {
    val scaledSize = recordedSize * 10
    val dest = new BufferedImage(500, 500, image.getType)
    val g = dest.getGraphics

    val hx = hotspot.x * 10
    val hy = hotspot.y * 10
    g.drawImage(image, 250-hx, 250-hy, scaledSize.x, scaledSize.y, null)
    g.dispose()
    CursorImageEntry(id, dest, imageHash, IntVect(250,250), recordedSize)
  }
  def savePng(filePath: Path): Unit = ImageIO.write(image, "png", filePath.toFile)
  def saveTiff(filePath: Path): Unit = ImageIO.write(image, "tiff", filePath.toFile)
}

object CursorImageEntry {
  case class ImgDetail(hotspot: IntVect, size: IntVect, scale: Double)

  case class Factory(detailMap: Map[Long, ImgDetail]) extends (ByteBuffer => CursorImageEntry) {
    def apply(buf: ByteBuffer): CursorImageEntry = {
      val id = buf.getUnsignedInt().toInt
      val length = buf.getUnsignedInt().toInt
      val imgBytes = Array.ofDim[Byte](length)
      buf.get(imgBytes)
      val imgHash = java.util.Arrays.hashCode(imgBytes) & 0x00000000ffffffffL
      val image = ImageIO.read(new ByteArrayInputStream(imgBytes))
      val detail = detailMap(id)

      CursorImageEntry(id, image, imgHash, detail.hotspot, detail.size)
    }
  }

  def buildDetailMap(entries: Seq[CursorEntry]): Map[Long, ImgDetail] = {
    val seq = for {
      entry <- entries if entry.imageId.isDefined
      imageId <- entry.imageId
    } yield imageId -> CursorImageEntry.ImgDetail(entry.hotspotRelative, entry.size, entry.scale)

    seq.toMap
  }
}
