package loki256.github.com.core.state

import loki256.github.com.core.compression.{Compressed, RLECompressor}

object CompressedStateHolder extends StateHolder {

  var state: Option[Seq[Compressed[String]]] = None

  override def getItemByIndex(index: Int): Option[String] = {
    state.flatMap { seq =>
      RLECompressor.compressedValueByIndex(seq, index)
    }
  }

  override def updateState(values: Array[String]): Unit = {
    state = Some(RLECompressor.compress[String](values))
  }

  override def isStateInitialized: Boolean = state.isDefined
}
