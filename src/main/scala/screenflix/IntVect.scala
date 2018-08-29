package screenflix

case class IntVect(x: Int, y: Int) {
  override def toString: String = s"⟨$x, $y⟩"
  def *(scalar: Int): IntVect = IntVect(x * scalar, y * scalar)
}
