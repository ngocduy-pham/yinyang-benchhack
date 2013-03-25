package dsl.regex

import ch.epfl.lamp.yinyang._
import ch.epfl.lamp.yinyang.api.CompiledStorage
import scala.collection.mutable.WeakHashMap
import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.tools.reflect.ToolBoxFactory

object `package` {

  val __compiledStorage = CompiledStorage

  def measureComputation[T](block: ⇒ T): T = macro _measureComputation[T]

  def measureGuard[T](block: ⇒ T): T = macro _measureGuard[T]

  def _measureComputation[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    new YYTransformer[c.type, T](c, "dsl.regex.RegexDSL", debug = true, rep = false, noGuard = true)(block)

  def _measureGuard[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
    new YYTransformer[c.type, T](c, "dsl.regex.RegexDSL", debug = true, rep = false, noGuard = false)(block)

  def matches(s: String, pattern: String): Boolean = ???

}
