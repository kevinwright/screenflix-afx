package screenflix

import java.nio.ByteBuffer

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}

sealed abstract class EventType(
  val value: Int,
  val name: String,
  val isMouse: Boolean = false,
  val isKeyboard: Boolean = false
) extends IntEnumEntry

case object EventType extends IntEnum[EventType] with IntCirceEnum[EventType] {
  case object Mystery            extends EventType(value = 0,  name = "mystery")
  case object LeftMouseDown      extends EventType(value = 1,  name = "leftMouseDown",     isMouse = true)
  case object LeftMouseUp        extends EventType(value = 2,  name = "leftMouseUp",       isMouse = true)
  case object RightMouseDown     extends EventType(value = 3,  name = "rightMouseDown",    isMouse = true)
  case object RightMouseUp       extends EventType(value = 4,  name = "rightMouseUp",      isMouse = true)
  case object MouseMoved         extends EventType(value = 5,  name = "mouseMoved",        isMouse = true)
  case object LeftMouseDragged   extends EventType(value = 6,  name = "leftMouseDragged",  isMouse = true)
  case object RightMouseDragged  extends EventType(value = 7,  name = "rightMouseDragged", isMouse = true)
  case object MouseEntered       extends EventType(value = 8,  name = "mouseEntered",      isMouse = true)
  case object MouseExited        extends EventType(value = 9,  name = "mouseExited",       isMouse = true)
  case object KeyDown            extends EventType(value = 10, name = "keyDown",           isKeyboard = true)
  case object KeyUp              extends EventType(value = 11, name = "keyUp",             isKeyboard = true)
  case object FlagsChanged       extends EventType(value = 12, name = "flagsChanged")
  case object AppKitDefined      extends EventType(value = 13, name = "appKitDefined")
  case object SystemDefined      extends EventType(value = 14, name = "systemDefined")
  case object ApplicationDefined extends EventType(value = 15, name = "applicationDefined")
  case object Periodic           extends EventType(value = 16, name = "periodic")
  case object CursorUpdate       extends EventType(value = 17, name = "cursorUpdate")
  case object Rotate             extends EventType(value = 18, name = "rotate")
  case object BeginGesture       extends EventType(value = 19, name = "beginGesture")
  case object EndGesture         extends EventType(value = 20, name = "endGesture")
  case object ScrollWheel        extends EventType(value = 22, name = "scrollWheel",       isMouse = true)
  case object TabletPoint        extends EventType(value = 23, name = "tabletPoint")
  case object TabletProximity    extends EventType(value = 24, name = "tabletProximity")
  case object OtherMouseDown     extends EventType(value = 25, name = "otherMouseDown",    isMouse = true)
  case object OtherMouseUp       extends EventType(value = 26, name = "otherMouseUp",      isMouse = true)
  case object OtherMouseDragged  extends EventType(value = 27, name = "otherMouseDragged", isMouse = true)
  case object Gesture            extends EventType(value = 29, name = "gesture")
  case object Magnify            extends EventType(value = 30, name = "magnify")
  case object Swipe              extends EventType(value = 31, name = "swipe")
  case object SmartMagnify       extends EventType(value = 32, name = "smartMagnify")
  case object QuickLook          extends EventType(value = 33, name = "quickLook")
  case object Pressure           extends EventType(value = 34, name = "pressure")
  case object DirectTouch        extends EventType(value = 37, name = "directTouch")

  val values = findValues

}

object Event {
  case class Factory(frameRate: Long) extends (ByteBuffer => Event) {
    def apply(buf: ByteBuffer): Event = {
      //  [mDataStream writeDouble:event->timestamp];
      //  [mDataStream writeUInt32:event->type];
      //  [mDataStream writeUInt32:event->modifierFlags];
      val rawTimestamp = buf.getDouble()
      val timestamp = Smpte.fromSecondsDouble(rawTimestamp, frameRate)
      val rawEventType = buf.getUInt32()
      println(s"rawEventType: $rawEventType")
      val eventType = EventType.withValue(rawEventType.toInt)
      val flags = buf.getUInt32()

      if(eventType.isMouse) {
        //[mDataStream writeFloat:event->absoluteLocation.x];
        //[mDataStream writeFloat:event->absoluteLocation.y];
        //[mDataStream writeUInt8:event->buttonNumber];
        //[mDataStream writeUInt8:event->clickCount];
        val location = FloatVect.getFromBuffer(buf)
        val button = buf.getUByte()
        val clickCount = buf.getUByte()
        MouseEvent(timestamp, eventType, flags, location, button, clickCount)
      } else if(eventType.isKeyboard) {
        //  [mDataStream writeUInt16:event->keyCode];
        //  [mDataStream writeBool:event->isARepeat];
        //  [mDataStream writeString:event->characters];
        //  [mDataStream writeString:event->charactersIgnoringModifiers];
        val keyCode = buf.getUInt16()
        val isRepeat = buf.getBool()
        val chars = buf.getNSStr()
        val charsWithoutModifiers = buf.getNSStr()
        KeyboardEvent(timestamp, eventType, flags, keyCode, isRepeat, chars, charsWithoutModifiers)
      } else {
        OtherEvent(timestamp, eventType, flags)
      }
    }
  }
}

sealed trait Event {
  def timestamp: Smpte
  def eventType: EventType
  def flags: Long
}


case class MouseEvent(
  timestamp: Smpte,
  eventType: EventType,
  flags: Long,
  location: FloatVect,
  button: Int,
  clickCount: Int,
) extends Event {
}

case class KeyboardEvent(
  timestamp: Smpte,
  eventType: EventType,
  flags: Long,
  keyCode: Long,
  isRepeat: Boolean,
  chars: String,
  charsWithoutModifiers: String,
) extends Event {

}

case class OtherEvent(
  timestamp: Smpte,
  eventType: EventType,
  flags: Long,
) extends Event

