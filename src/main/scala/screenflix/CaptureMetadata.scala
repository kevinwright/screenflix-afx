package screenflix

import java.io.File
import java.nio.file.Path
import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import com.dd.plist._

case class CaptureMetadata(
  averageCaptureRate: Double,
  captureRate: Long,
  date: ZonedDateTime,
  duration: Smpte,
  numberOfFrames: Long,
  framesContainCursor: Boolean,
  hasAudio: Boolean,
  hasVideo: Boolean,
  height: Long,
  width: Long,
  isValid: Boolean,
  scale: Long,
  screenflickVersion: String,
  uuid: UUID
)

object CaptureMetadata {
  def load(filePath: Path): CaptureMetadata = {
    val rootDict = PropertyListParser.parse(filePath.toFile).asInstanceOf[NSDictionary]

    val averageCaptureRate = rootDict.objectForKey("averageCaptureRate").asInstanceOf[NSNumber].doubleValue
    val captureRate = rootDict.objectForKey("captureRate").asInstanceOf[NSNumber].longValue
    val date = rootDict.objectForKey("date").asInstanceOf[NSDate].getDate.toInstant.atZone(ZoneId.of("UTC"))
    val duration = Smpte.fromSecondsDouble(rootDict.objectForKey("duration").asInstanceOf[NSNumber].doubleValue, captureRate)
    val numberOfFrames = rootDict.objectForKey("numberOfFrames").asInstanceOf[NSNumber].longValue
    val framesContainCursor = rootDict.objectForKey("framesContainCursor").asInstanceOf[NSNumber].boolValue
    val hasAudio = rootDict.objectForKey("hasAudio").asInstanceOf[NSNumber].boolValue
    val hasVideo = rootDict.objectForKey("hasVideo").asInstanceOf[NSNumber].boolValue
    val height = rootDict.objectForKey("height").asInstanceOf[NSNumber].longValue
    val width = rootDict.objectForKey("width").asInstanceOf[NSNumber].longValue
    val isValid = rootDict.objectForKey("isValid").asInstanceOf[NSNumber].boolValue
    val scale = rootDict.objectForKey("scale").asInstanceOf[NSNumber].longValue
    val screenflickVersion = rootDict.objectForKey("screenflickVersion").asInstanceOf[NSString].toString
    val uuid = UUID.fromString(rootDict.objectForKey("uuid").asInstanceOf[NSString].toString)

    CaptureMetadata(
      averageCaptureRate,
      captureRate,
      date,
      duration,
      numberOfFrames,
      framesContainCursor,
      hasAudio,
      hasVideo,
      height,
      width,
      isValid,
      scale,
      screenflickVersion,
      uuid
    )
  }
}
