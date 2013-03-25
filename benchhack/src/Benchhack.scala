import java.io.FileWriter
import java.io.BufferedWriter
import java.io.PrintWriter
import dsl.regex._
import org.scalameter.api.SeparateJvmsExecutor
import org.scalameter.api.LocalExecutor
import org.scalameter.api.Executor
import org.scalameter.api.Aggregator
import org.scalameter.api.PerformanceTest
import org.scalameter.api.Measurer
import org.scalameter.api.Reporter
import org.scalameter.api.Persistor
import org.scalameter.api.Gen
import org.scalameter.api.exec
import org.scalameter.api.machine
import org.scalameter.CurveData
import org.scalameter.utils.Tree
import org.scalameter.Context

object __Input {
  var s = ""
}

object Benchhack extends PerformanceTest {

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(raw"D:\enjoy\mpde\regexGuardMPDE.benchmark", true)))
      // output context
      println(s"::Benchmark ${result.context.scope}::")
      //stream.println(s"::Benchmark ${result.context.scope}::")
      for ((key, value) ← result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
        println(s"$key: $value")
        //stream.println(s"$key: $value")
      }

      // output measurements
      for (measurement ← result.measurements) {
        println(s"${measurement.params}: ${measurement.time}")
        stream.println(s"${measurement.time}")
      }

      // add a new line
      println("")
      stream.close()
    }

    def report(result: Tree[CurveData], persistor: Persistor) = true

  }

  lazy val persistor = Persistor.None

  val runs = Gen.single("runs")(1)

  performance of "Yingyang approach" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1) in {

      var input = "input"
      for (mult ← 2801 to 4000 by 200) {

        measure method "guard" in {
          using(runs) setUp (_ ⇒ input = "input" * (mult * mult)) in { loop ⇒
            measureGuard {
              matches(input, "pattern")
            }
          }
        }

      }
    }

}
