package loki256.github.com.core.state


object SimpleStateHolder extends StateHolder {
  /// Hold everything as Array, fast get operation

  // state
  var state: Option[Array[String]] = None

  def getItemByIndex(index: Int): Option[String] = {
    state.flatMap { arr => arr.lift(index) }
  }

  def updateState(values: Array[String]) = {
    state = Some(values)
  }

  def isStateInitialized: Boolean = state.isDefined
}
