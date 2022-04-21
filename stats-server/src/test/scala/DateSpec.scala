import com.github.kineticcookie.project.stats_server.services.WalletStatsServiceImpl.hourly
import org.scalatest.funspec.AnyFunSpec

import java.time.Instant
import java.time.temporal.ChronoUnit

class DateSpec extends AnyFunSpec {
  describe("Dates") {
    it("should iterate hourly") {
      val begin = Instant.now().minus(1, ChronoUnit.DAYS)
      val end = Instant.now()
      val spans = hourly(begin, end)
      println(s"start: ${begin}")
      println(spans.mkString("\n"))
      println(s"end: $end")
      assert(spans.exists(_.equals(begin)))
    }
  }

}
