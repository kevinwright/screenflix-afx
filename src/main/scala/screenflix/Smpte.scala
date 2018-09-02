package screenflix



object Smpte {
  private val SecondsPerMinute = 60L
  private val MinutesPerHour = 60L

  def fromSecondsDouble(t: Double, frameRate: Long): Smpte = {
    val framesTotal = math.round(t * frameRate)

    val frames = framesTotal % frameRate
    val secsTotal = framesTotal / frameRate
    val secs = secsTotal % SecondsPerMinute
    val minsTotal = secsTotal / SecondsPerMinute
    val mins = minsTotal % MinutesPerHour
    val hours = minsTotal / MinutesPerHour

    Smpte(
      hours.toInt,
      mins.toInt,
      secs.toInt,
      frames.toInt,
      frameRate.toInt
    )
  }
}

import Smpte._

case class Smpte(h: Int, m: Int, s: Int, f: Int, frameRate: Int) {
  override def toString: String = f"$h:$m%02d:$s%02d:$f%03d"

  def inSeconds: Double = {
    val wholeMins = (h * MinutesPerHour) + m
    val wholeSecs = (wholeMins * SecondsPerMinute) + s
    wholeSecs + (f.toDouble / frameRate.toDouble)
  }

  def inFrames: Long = {
    val wholeMins = (h * MinutesPerHour) + m
    val wholeSecs = (wholeMins * SecondsPerMinute) + s
    (wholeSecs * frameRate) + f
  }
}
