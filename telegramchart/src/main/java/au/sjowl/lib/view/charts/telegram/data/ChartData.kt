package au.sjowl.lib.view.charts.telegram.data

class ChartData {

    val title: String = "Followers"

    val columns: MutableMap<String, ChartLineData> = mutableMapOf()

    val time: ChartAxisXData = ChartAxisXData()

    var valueMin: Int = 0

    var valueMax: Int = 0

    var timeIndexStart = 0

    var timeIndexEnd = 0
        set(value) {
            field = if (value < timeIndexStart) timeIndexStart else value
        }

    var scaleInProgress = false

    val valueInterval get() = valueMax - valueMin

    val timeStart get() = time.values[timeIndexStart]

    val timeEnd get() = time.values[timeIndexEnd]

    val timeInterval get() = timeEnd - timeStart

    val timeIntervalIndexes get() = timeIndexEnd - timeIndexStart

    var pointerTimeIndex = 0

    var pointerTimeX = 0f

    fun initTimeWindow() {
        timeIndexStart = Math.max(time.values.lastIndex - 60, 0)
        timeIndexEnd = time.values.lastIndex
    }
}