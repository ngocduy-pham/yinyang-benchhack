package dsl.regex

import ch.epfl.lamp.yinyang.api._
import base._
import scala.collection._

/** The int printing DSL */
trait RegexDSL
  extends ScalaCompile with CodeGenerator with MiniStringDSL with Interpreted with BaseYinYang with Base {

  var sb: StringBuffer = new StringBuffer()

  def matches(s: String, pattern: String) =
    sb.append(s"(new scala.util.matching.Regex(${pattern.toString()}) findFirstIn ${s.toString()}).isEmpty;\n")

  def reset() = {
    sb = new StringBuffer()
    holes.clear
  }

  def stagingAnalyze(): List[scala.Int] = {
    reset()
    main()

    holes.toList map (_.symbolId)
  }

  def generateCode(className: scala.Predef.String): scala.Predef.String = {
    reset()
    val res = main()
    val distinctHoles = holes.distinct
    s"""
      class $className extends Function${distinctHoles.size}[${"String, " * distinctHoles.size} Boolean] {
        def apply(${distinctHoles.map(y ⇒ y.toString + ": " + y.tpe.toString).mkString("", ",", "")}) = {
          ${sb.toString} 
          //${res.toString}
        }
      }
    """
  }

  override def interpret[T: Manifest](params: Any*): T = {
    if (compiledCode == null) {
      compiledCode = compile[T, () ⇒ T]
    }
    compiledCode.apply().asInstanceOf[T]
  }

  var compiledCode: () ⇒ Any = _
}

trait MiniStringDSL extends BaseYinYang { self: CodeGenerator ⇒

  type String = StringOps

  val holes = new mutable.ArrayBuffer[Hole]()

  trait StringOps {
    def +(that: String): String = StringPlus(StringOps.this, that)
    def value: scala.Predef.String
  }

  // actual classes that provide lifting
  case class StringConst(i: scala.Predef.String) extends StringOps {
    override def toString = "\"" + i + "\""
    def value = i
  }

  case class StringPlus(l: StringOps, r: StringOps) extends StringOps {
    override def toString = s"($l + $r)"
    def value = "stupid"
  }

  implicit object LiftString extends LiftEvidence[scala.Predef.String, String] {
    def lift(v: scala.Predef.String): String = StringConst(v)
    def hole(tpe: Manifest[Any], symbolId: scala.Int): String = {
      val h = Hole(tpe, symbolId)
      holes += h
      h
    }
  }

  case class Hole(tpe: Manifest[Any], symbolId: scala.Int) extends StringOps {
    override def toString = "x" + symbolId
    def value = "{a hole}"
  }

}
