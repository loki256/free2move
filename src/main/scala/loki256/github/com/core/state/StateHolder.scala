package loki256.github.com.core.state

trait StateHolder {
  def getItemByIndex(index: Int): Option[String]
  def updateState(values: Array[String]): Unit
  def isStateInitialized: Boolean
}
