package screenflix

import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.{ByteBuffer, ByteOrder, MappedByteBuffer}

object BinaryFileParser {
  def parse[T](fileName: String, parseBlock: ByteBuffer => T, expectedHeader: String = ""): Seq[T] = {
    val file = new RandomAccessFile(fileName, "r")
    try {
      val buf: MappedByteBuffer = file.getChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length)
      buf.order(ByteOrder.nativeOrder())
      if (expectedHeader.nonEmpty) {
        val header: String = buf.getStr(expectedHeader.length)
        assert(header == expectedHeader)
      }

      val sb = Seq.newBuilder[T]
      while (buf.hasRemaining) {
        sb += parseBlock(buf)
      }
      sb.result()
    } finally {
      file.close()
    }
  }
}
