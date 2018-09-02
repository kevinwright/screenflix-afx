package screenflix

import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.{ByteBuffer, ByteOrder, MappedByteBuffer}

object BinaryFileParser {
  def parse[T](
    filePath: Path,
    parseBlock: ByteBuffer => T,
    expectedHeader: String = "",
    skipPreamble: Boolean = false
  ): Seq[T] = {
    val file = new RandomAccessFile(filePath.toFile, "r")
    try {
      val buf: MappedByteBuffer = file.getChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length)
      buf.order(ByteOrder.nativeOrder())
      if (expectedHeader.nonEmpty) {
        val header: String = buf.getStr(expectedHeader.length)
        assert(header == expectedHeader, s"Expected header: $expectedHeader not found.")
      }

      if(skipPreamble) {
        val preamble = Array.ofDim[Byte](24)
        buf.get(preamble)
        assert(preamble.head == (1: Byte))
        assert(preamble.tail.forall(_ == (0: Byte)))
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
