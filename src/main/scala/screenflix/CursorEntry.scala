package screenflix

import java.nio.ByteBuffer

case class CursorEntry(
  timestamp       : Smpte,
  imageId         : Option[Long],
  scale           : Float,
  hotspotRelative : IntVect,
  origin          : FloatVect,
  size            : IntVect,
) {
  def hotspotAbsolute: IntVect = IntVect(
    math.round(origin.x + hotspotRelative.x),
    math.round(origin.y + size.y - hotspotRelative.y)
  )
  override def toString: String = {
    f"$timestamp - image: ${imageId getOrElse "∅"} - scale: $scale - size: $size - pos: $hotspotAbsolute"
  }
}

object CursorEntry {
  case class Factory(frameRate: Long) extends (ByteBuffer => CursorEntry) {
    def apply(buf: ByteBuffer): CursorEntry = {
      buf.getInt() //reserved

      val rawTimestamp = buf.getDouble()
      val timestamp = Smpte.fromSecondsDouble(rawTimestamp, frameRate)
      val visible = buf.get() > 0
      val scale = buf.getFloat()
      val hotspot = FloatVect.getFromBuffer(buf).toIntVect
      val origin = FloatVect.getFromBuffer(buf)
      val size = FloatVect.getFromBuffer(buf).toIntVect

      val imageId = if (visible) Some(buf.getUnsignedInt()) else None

      CursorEntry(timestamp, imageId, scale, hotspot, origin, size)
    }
  }
}

