import java.nio.ByteBuffer

package object screenflix {
  implicit class RichByteBuffer(val bb: ByteBuffer) extends AnyVal {
    def getStr(len: Int): String = {
      val bytes = (1 to len).map(_ => bb.get())
      bytes.flatMap(Character.toChars(_).toSeq).mkString("")
    }

    def getUnsignedInt(): Long = bb.getInt() & 0x00000000ffffffffL
  }

}
