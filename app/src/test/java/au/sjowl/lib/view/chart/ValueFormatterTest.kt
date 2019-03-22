package au.sjowl.lib.view.chart

import au.sjowl.lib.view.telegramchart.view.ValueFormatter
import org.amshove.kluent.shouldBe
import org.junit.Test
import kotlin.test.assertEquals

class ValueFormatterTest {
    val formatter = ValueFormatter()

    @Test
    fun marksFromRangeTest() {
        assertEquals(formatter.marksFromRange(1, 250, 6), arrayListOf(0, 50, 100, 150, 200, 250))
        assertEquals(formatter.marksFromRange(1, 252, 6), arrayListOf(0, 60, 120, 180, 240, 300))
        assertEquals(formatter.marksFromRange(1, 50, 6), arrayListOf(0, 10, 20, 30, 40, 50))
        assertEquals(formatter.marksFromRange(1, 100, 6), arrayListOf(0, 20, 40, 60, 80, 100))
        assertEquals(formatter.marksFromRange(1, 105, 6), arrayListOf(0, 25, 50, 75, 100, 125))
        assertEquals(formatter.marksFromRange(1, 102, 6), arrayListOf(0, 25, 50, 75, 100, 125))
        assertEquals(formatter.marksFromRange(1, 110, 6), arrayListOf(0, 25, 50, 75, 100, 125))
        assertEquals(formatter.marksFromRange(5, 99, 6), arrayListOf(0, 20, 40, 60, 80, 100))
        assertEquals(formatter.marksFromRange(26, 278, 6), arrayListOf(0, 60, 120, 180, 240, 300))
    }

    @Test
    fun stepFromRangeTest() {
        formatter.stepFromRange(1, 250, 6) shouldBe 50
        formatter.stepFromRange(10, 250, 6) shouldBe 50
        formatter.stepFromRange(10, 230, 6) shouldBe 50
        formatter.stepFromRange(5, 99, 6) shouldBe 20
        formatter.stepFromRange(5, 10, 6) shouldBe 1
        formatter.stepFromRange(5, 200, 6) shouldBe 40
    }

    @Test
    fun formatRangeTest() {
        assertEquals("1.2k", formatter.format(1200))
        assertEquals("1k", formatter.format(1000))
        assertEquals("5", formatter.format(5))
        assertEquals("403", formatter.format(403))
        assertEquals("2.3k", formatter.format(2300))
        assertEquals("23k", formatter.format(23001))
        assertEquals("2.1M", formatter.format(2100000))
        assertEquals("21M", formatter.format(21000000))
        assertEquals("21M", formatter.format(21000001))
        assertEquals("21.4M", formatter.format(21400001))
    }
}