package loki256.github.com.core.compression

trait Compressor {
  def compress[A]: Seq[A] => Seq[Compressed[A]]
  def decompress[A]: Seq[Compressed[A]] => Seq[A]
}

sealed trait Compressed[+A]
case class Single[A](element: A) extends Compressed[A]
case class Repeat[A](count: Int, element: A) extends Compressed[A]
