package screenflix

import java.nio.ByteBuffer

extension (bb: ByteBuffer) {
  def getStr(len: Int): String = {
    val bytes = (1 to len).map(_ => bb.get())
    bytes.flatMap(Character.toChars(_).toSeq).mkString("")
  }

  def getBool(): Boolean = bb.get > 0

  def getNSStr(): String = {
    val prefix: Array[Byte] = Array.ofDim[Byte](5)
    bb.get(prefix)
    if(!prefix.sameElements(Array(1,1,0,0,0).map(_.toByte))) {
      sys.error(s"NSString prefix was unexpected: ${prefix.mkString(" ")}")
    }
    bb.get().toChar.toString
  }

  def getUInt32(): Long = 0x00000000ffffffffL & bb.getInt()

  def getUInt16(): Int = 0xFFFF & bb.getShort()

  def getUByte(): Int = 0xff & bb.get()
}

