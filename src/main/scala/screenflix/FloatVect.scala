package screenflix

import java.nio.ByteBuffer

case class FloatVect(x: Float, y: Float) {
  override def toString: String = s"⟨$x, $y⟩"
  def toIntVect: IntVect = IntVect(x.toInt, y.toInt)
}

object FloatVect {
  def getFromBuffer(buf: ByteBuffer): FloatVect = {
    val x = buf.getFloat()
    val y = buf.getFloat()
    FloatVect(x, y)
  }
}
