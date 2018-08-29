package screenflix

import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import java.nio.ByteBuffer
import java.io.{ByteArrayInputStream, File}


case class CursorImageEntry(id: Int, image: BufferedImage) {
  def normalised(size: IntVect, hotspot: IntVect): CursorImageEntry = {
    val scaledSize = size * 10

    val dest = new BufferedImage(500, 500, image.getType)
    val g = dest.getGraphics
    g.drawImage(image, 250-hotspot.x, 250-hotspot.y, scaledSize.x, scaledSize.y, null)
    g.dispose
    CursorImageEntry(id, dest)
  }
  def savePng(fileName: String): Unit = ImageIO.write(image, "png", new File(fileName))
  def saveTiff(fileName: String): Unit = ImageIO.write(image, "tiff", new File(fileName))
}

object CursorImageEntry {
  def getFromBuffer(buf: ByteBuffer): CursorImageEntry = {
    val id = buf.getUnsignedInt().toInt
    val length = buf.getUnsignedInt().toInt
    val imgBytes = Array.ofDim[Byte](length)
    buf.get(imgBytes)
    val image = ImageIO.read(new ByteArrayInputStream(imgBytes))
    CursorImageEntry(id, image)
  }
}
