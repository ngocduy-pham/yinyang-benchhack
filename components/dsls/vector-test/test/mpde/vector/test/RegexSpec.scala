package mpde.vector.test

import org.scalatest._
import dsl.regex._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegexSpec extends FlatSpec with ShouldMatchers {

  "Static code staging" should "work" in {
    val s = "shit"
    val x = liftRegexDebug {
      matches("abc" + s, "xyz")
    }
    assert(x == true, "Should return the value 7!");
  }

}