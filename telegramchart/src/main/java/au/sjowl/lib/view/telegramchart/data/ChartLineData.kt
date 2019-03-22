package au.sjowl.lib.view.telegramchart.data

import android.graphics.Color
import androidx.annotation.ColorInt

class ChartLineData(
    val id: String
) {

    var type: String = "line"

    var name: String = ""

    val values: ArrayList<Int> = arrayListOf() // todo int array

    @ColorInt
    var color: Int = Color.RED

    var enabled: Boolean = true

    var min: Int = 0

    var max: Int = 0

    val height get() = max - min

    var chartMin = 0

    var chartMax = 0

    fun calculateBorders(start: Int = 0, end: Int = values.size - 1) {
        min = Int.MAX_VALUE
        max = Int.MIN_VALUE

        for (i in start..end) {
            val v = values[i]
            if (v < min) min = v
            if (v > max) max = v
        }
    }

    fun calculateExtremums() {
        chartMin = Int.MAX_VALUE
        chartMax = Int.MIN_VALUE

        for (i in 0 until values.size) {
            val v = values[i]
            if (v < chartMin) chartMin = v
            if (v > chartMax) chartMax = v
        }
    }
}