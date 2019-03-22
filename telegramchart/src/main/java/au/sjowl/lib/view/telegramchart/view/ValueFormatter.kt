package au.sjowl.lib.view.telegramchart.view

class ValueFormatter {
    fun stepFromRange(min: Int, max: Int, marksSize: Int): Int {
        val interval = max - min

        var i = 0
        var stop = false
        var s = 0

        while (!stop) {
            s = stepFromIndex(i)
            val t = interval / s

            for (j in 1..marksSize * 2) {
                if (t in 0..marksSize * j) {
                    s *= j
                    stop = true
                    break
                }
            }
            i++
        }
        return s
    }

    fun marksFromRange(min: Int, max: Int, marksSize: Int): ArrayList<Int> {
        val step = stepFromRange(min, max, marksSize)
        val minAdjusted = min - min % step
        val maxAdjusted = if (max % step == 0) max else max - (max + step) % step + step
        val stepAdjusted = stepFromRange(minAdjusted, maxAdjusted, marksSize)

        val list = arrayListOf<Int>()
        for (i in 0..5) list.add(stepAdjusted * i + minAdjusted)

        return list
    }

    fun format(value: Int): String {
        return value.toString()
//        return when (value) {
//            in 0..999 -> value.toString()
//            in 1000..999_999 -> "${removeTrailingZeroes("%.1f".format(value / 1000f).toFloat())}k"
//            in 1_000_000..999_999_999 -> "${removeTrailingZeroes("%.1f".format(value / 1_000_000f).toFloat())}M"
//            else -> value.toString()
//        }
    }

    private inline fun removeTrailingZeroes(v: Float): String =
        if (v == v.toLong().toFloat()) String.format("%d", v.toLong())
        else String.format("%s", v)

    private fun stepFromIndex(index: Int): Int {
        return if (index == 0) {
            1
        } else {
            val i = index - 1
            (i + 1) % 2 * 5 * Math.pow(10.0, (i / 2).toDouble()).toInt() + (i) % 2 * 10 * Math.pow(10.0, (i / 2).toDouble()).toInt()
        }
    }
}