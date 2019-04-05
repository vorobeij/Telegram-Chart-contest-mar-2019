package au.sjowl.lib.view.charts.telegram.time

import au.sjowl.lib.view.charts.telegram.DateFormatter

data class ScalablePoint(
    var t: Long = 0L,
    var xStart: Float = 0f,
    var x: Float = xStart, // [0;1]
    var alpha: Float = 1f,
    var interval: Long = 0L,
    var start: Long = 0L,
    var toFade: Boolean = false
) {
    val title: String get() = DateFormatter.format(t)
}