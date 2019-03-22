package au.sjowl.lib.view.telegramchart.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.params.ChartLayoutParams
import au.sjowl.lib.view.telegramchart.params.ChartPaints

class AxisY(
    private val chartLayoutParams: ChartLayoutParams,
    var paints: ChartPaints,
    var chartData: ChartData
) {

    private val valueFormatter = ValueFormatter()

    private var pointsFrom = arrayListOf<CanvasPoint>().apply {
        repeat(chartLayoutParams.yMarks + 1) {
            add(CanvasPoint())
        }
    }

    private var pointsTo = arrayListOf<CanvasPoint>().apply {
        repeat(chartLayoutParams.yMarks + 1) {
            add(CanvasPoint())
        }
    }

    private val historyRange = HistoryRange()

    private var animFloat = 0f

    private val rect = Rect()

    private val paddingRect = PaddingRect(chartLayoutParams.paddingTextBottom / 2)

    fun drawMarks(canvas: Canvas) {
        val x = chartLayoutParams.paddingTextBottom * 1f

        paints.paintChartText.alpha = ((1f - animFloat) * 255).toInt()
        paints.paintMarksBackground.alpha = paints.paintChartText.alpha
        for (i in 0 until pointsTo.size) {
            val text = valueFormatter.format(pointsTo[i].value)
            paints.paintChartText.getTextBounds(text, 0, text.length, rect)
            val y = pointsTo[i].canvasValue - chartLayoutParams.paddingTextBottom
            val r = x / 2
            canvas.drawRoundRect(paddingRect.rect(rect, x.toInt(), y.toInt()), r, r, paints.paintMarksBackground)
            canvas.drawText(text, x, y, paints.paintChartText)
        }

        paints.paintChartText.alpha = (animFloat * 255).toInt()
        for (i in 0 until pointsFrom.size) {
            canvas.drawText(valueFormatter.format(pointsFrom[i].value), x, pointsFrom[i].canvasValue - chartLayoutParams.paddingTextBottom, paints.paintChartText)
        }
    }

    fun drawGrid(canvas: Canvas) {
        paints.paintGrid.alpha = ((1f - animFloat) * 255).toInt()
        for (i in 0 until pointsTo.size) {
            canvas.drawLine(0f, pointsTo[i].canvasValue, chartLayoutParams.w, pointsTo[i].canvasValue, paints.paintGrid)
        }

        paints.paintGrid.alpha = (animFloat * 255).toInt()
        for (i in 0 until pointsFrom.size) {
            canvas.drawLine(0f, pointsFrom[i].canvasValue, chartLayoutParams.w, pointsFrom[i].canvasValue, paints.paintGrid)
        }
    }

    fun updateStartPoints() {
        for (i in 0 until pointsTo.size) {
            pointsFrom[i].value = pointsTo[i].value
            pointsFrom[i].canvasValue = pointsTo[i].canvasValue
        }

        historyRange.minStart = chartData.valueMin
        historyRange.maxStart = chartData.valueMax
    }

    fun adjustValuesRange(min: Int, max: Int) {
        val marks = valueFormatter.marksFromRange(min, max, chartLayoutParams.yMarks)
        for (i in 0 until marks.size) {
            pointsTo[i].value = marks[i]
        }

        chartData.valueMin = pointsTo[0].value
        chartData.valueMax = pointsTo.last().value

        historyRange.minEnd = chartData.valueMin
        historyRange.maxEnd = chartData.valueMax
    }

    fun onAnimateValues(v: Float) { // v: 1 -> 0
        animFloat = v
        val kY = 1f * (chartLayoutParams.h - chartLayoutParams.paddingBottom - chartLayoutParams.paddingTop) / (historyRange.endInterval - historyRange.deltaInterval * v)
        val mh = chartLayoutParams.h * 1f - chartLayoutParams.paddingBottom
        var min = historyRange.minEnd + v * historyRange.deltaMin
        // scale new points
        pointsTo.forEach { point -> point.canvasValue = mh - kY * (point.value - min) }
        // rescale old points
        min = historyRange.minEnd + (1f - v) * historyRange.deltaMin
        pointsFrom.forEach { point -> point.canvasValue = mh - kY * (point.value - min) }
    }
}

private class HistoryRange {
    var minStart = 0
    var minEnd = 0
    var maxStart = 0
    var maxEnd = 0
    val startInterval get() = maxStart - minStart
    val endInterval get() = maxEnd - minEnd
    val deltaInterval get() = endInterval - startInterval
    val deltaMin get() = minEnd - minStart
}

private class CanvasPoint(
    var value: Int = 0,
    var canvasValue: Float = 0f
)

private class PaddingRect(var padding: Int = 0) {
    private var rF = RectF()

    fun rect(r: Rect, dx: Int, dy: Int): RectF {
        rF.left = (r.left - padding + dx).toFloat()
        rF.right = (r.right + padding + dx).toFloat()
        rF.top = (r.top - padding + dy).toFloat()
        rF.bottom = (r.bottom + padding + dy).toFloat()
        return rF
    }
}