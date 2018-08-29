package screenflix



object Smpte {
  private val MillisPerSecond = 1000L
  private val SecondsPerMinute = 60L
  private val MinutesPerHour = 60L

  def fromSecondsDouble(sd: Double, frameRate: Long): Smpte = {
    val wholeSeconds = sd.toLong

    val secondsPart = wholeSeconds % SecondsPerMinute
    val wholeMinutes = wholeSeconds / SecondsPerMinute
    val minutesPart = wholeMinutes % MinutesPerHour
    val hoursPart = wholeMinutes / MinutesPerHour

    val frames = math.round((sd - wholeSeconds.toDouble) * frameRate)
    Smpte(
      hoursPart.toInt,
      minutesPart.toInt,
      secondsPart.toInt,
      frames.toInt,
      frameRate.toInt
    )
  }
}

import Smpte._

case class Smpte(h: Int, m: Int, s: Int, f: Int, frameRate: Int) {
  override def toString: String = f"$h:$m%02d:$s%02d:$f%03d"

  def toFrame: Long = {
    val wholeMins = (h * MinutesPerHour) + m
    val wholeSecs = (wholeMins * SecondsPerMinute) + s
    (wholeSecs * frameRate) + f
  }
}
