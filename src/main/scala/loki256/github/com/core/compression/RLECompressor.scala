package loki256.github.com.core.compression


object RLECompressor extends Compressor {

  override def compress[A]: (Seq[A]) => Seq[Compressed[A]] = (seq: Seq[A]) => {
    seq.foldRight[List[Repeat[A]]](List[Repeat[A]]())((item, lst) => {
      lst match {
        case Nil =>
          List(Repeat(1, item))
        case x :: tail =>
          if (item == x.element) {
            Repeat(x.count + 1, item) :: tail
          } else {
            Repeat(1, item) +: lst
          }
      }
    }).map {
      // not really necessary, Repeat(1, x) works find...
      case Repeat(1, x) => Single(x)
      case other => other
    }
  }

  override def decompress[A]: (Seq[Compressed[A]]) => Seq[A] = (seq: Seq[Compressed[A]]) => {
    seq.flatMap {
      case Single(item) => Seq(item)
      case Repeat(count: Int, item) => Seq.fill(count)(item)
    }
  }

  // return value from compressed sequence by index
  def compressedValueByIndex[A]: ((Seq[Compressed[A]], Int) => Option[A]) = (seq: Seq[Compressed[A]], index: Int) => {

    val view = seq.toIterator.flatMap {
      case Single(item) => Seq(item)
      case Repeat(count: Int, item) => Seq.fill(count)(item)
    }.zipWithIndex.takeWhile(_._2 <= index).toList

    view.lastOption.flatMap { x =>
      if (x._2 == index) Some(x._1)
      else None
    }
  }
}
