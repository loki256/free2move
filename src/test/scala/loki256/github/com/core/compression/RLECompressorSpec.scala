package loki256.github.com.core.compression

import org.scalatest.{FunSpec, Matchers}

class RLECompressorSpec extends FunSpec with Matchers {

  describe("compress") {
    val compress = RLECompressor.compress[String]

    it("should compress empty sequence") {
      val result = compress(Seq())
      result.size should be (0)
    }

    it("should compress repeating values") {
      val result = compress(Seq("1", "1", "1", "1"))
      result.size should be (1)
      result.head.asInstanceOf[Repeat[String]] should be (Repeat[String](4, "1"))
    }

    it("should keep correct order") {
      val result = compress(Seq("1", "1", "1", "2", "3", "3", "3"))
      result.size should be (3)
      result.head.asInstanceOf[Repeat[String]] should be (Repeat[String](3, "1"))
      result(1).asInstanceOf[Single[String]] should be (Single[String]("2"))
      result(2).asInstanceOf[Repeat[String]] should be (Repeat[String](3, "3"))
    }
  }

  describe("decompress") {

    val decompress = RLECompressor.decompress[String]
    it ("should decompress empty sequence") {
      val result = decompress(Seq())
      result.size should be (0)
    }

    it("should decompress simple case") {
      val result = decompress(Seq(Repeat(2, "1"), Single("3")))
      result.size should be (3)
      result.head should be ("1")
      result(1) should be ("1")
      result(2) should be ("3")
    }

    it("should decompress other simple case") {
      val result = decompress(Seq(Repeat(2, "1"), Repeat(3000, "A")))
      result.size should be (3002)
      result.head should be ("1")
      result(1) should be ("1")
      result(3001) should be ("A")
    }
  }

  describe("compressedIndex") {
    val compressIndex = RLECompressor.compressedValueByIndex[String]
    it("should return None if sequence is empty") {
      val result = compressIndex(Seq(), 0)
      result should be (None)
    }

    it("should correct values for single element") {
      val seq = Seq(Single("A"))
      compressIndex(seq, 0) should be (Some("A"))
      compressIndex(seq, 1) should be (None)
    }

    it("should return value by index within sequence") {
      val sequence = Seq(Repeat(2, "A"), Single("B"), Repeat(2, "C"))
      compressIndex(sequence, 0) should be (Some("A"))
      compressIndex(sequence, 1) should be (Some("A"))
      compressIndex(sequence, 2) should be (Some("B"))
      compressIndex(sequence, 3) should be (Some("C"))
      compressIndex(sequence, 4) should be (Some("C"))
      compressIndex(sequence, 5) should be (None)
      compressIndex(sequence, 500) should be (None)
    }

  }

}
