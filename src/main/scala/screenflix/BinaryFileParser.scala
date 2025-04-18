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
    preambleByteLength: Int = 0
  ): Seq[T] = {
    val file = new RandomAccessFile(filePath.toFile, "r")
    try {
      val buf: MappedByteBuffer = file.getChannel.map(FileChannel.MapMode.READ_ONLY, 0, file.length)
      buf.order(ByteOrder.nativeOrder())
      if (expectedHeader.nonEmpty) {
        val header: String = buf.getStr(expectedHeader.length)
        assert(header == expectedHeader, s"Expected header: $expectedHeader not found.")
      }

      if(preambleByteLength > 0) {
        val preamble = Array.ofDim[Byte](preambleByteLength)
        buf.get(preamble)
//        assert(preamble.head == (1: Byte), s"Expected preamble for ${filePath} to start with 1")
//        assert(preamble.tail.forall(_ == (0: Byte)), s"Expected preamble for ${filePath} to end with 0's")
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
