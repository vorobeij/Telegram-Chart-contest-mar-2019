package au.sjowl.lib.view.charts.telegram.view

import android.graphics.Canvas
import android.graphics.Path
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.data.ChartLineData
import au.sjowl.lib.view.charts.telegram.params.ChartLayoutParams
import au.sjowl.lib.view.charts.telegram.params.ChartPaints

class Chart(
    val lineData: ChartLineData,
    val chartLayoutParams: ChartLayoutParams,
    var paints: ChartPaints,
    val chartData: ChartData
) {

    protected val path = Path()

    protected var enabled = lineData.enabled

    protected var alpha = 1f

    private val points = FloatArray(lineData.values.size * 2)

    private val pointsFrom = FloatArray(lineData.values.size * 2)

    private val drawingPoints = FloatArray(lineData.values.size * 2)

    private var w = 0f

    private var h = 0f

    private var timeStart = 0

    private var timeEnd = 0

    private var xmin = 0L

    private var mh = 0f

    private var kX = 0f

    private var kY = 0f

    private var animValue = 0f

    fun setupPoints() {
        calculatePoints()
        for (i in 2 * timeStart..(2 * timeEnd + 1)) {
            drawingPoints[i] = points[i]
        }
        updatePathFromPoints()
    }

    fun updateStartPoints() {
        for (i in 2 * timeStart..(2 * timeEnd + 1)) {
            pointsFrom[i] = drawingPoints[i]
        }
        enabled = lineData.enabled
    }

    fun updateFinishState() {
        calculatePoints()
    }

    fun onAnimateValues(v: Float) {
        alpha = when {
            lineData.enabled && enabled -> 1f
            lineData.enabled && !enabled -> 1f - v
            !lineData.enabled && !enabled -> 0f
            else -> v
        }
        animValue = v
        for (i in 2 * timeStart..2 * timeEnd step 2) {
            drawingPoints[i] = points[i]
            drawingPoints[i + 1] = points[i + 1] + (pointsFrom[i + 1] - points[i + 1]) * v
        }
        updatePathFromPoints()
    }

    fun draw(canvas: Canvas) {
        if (lineData.enabled || enabled) {
            val paint = paints.paintChartLine
            paint.color = lineData.color
            paint.alpha = (alpha * 255).toInt()
            canvas.drawPath(path, paint)
        }
    }

    fun drawPointer(canvas: Canvas) {
        if (!lineData.enabled) return
        paints.paintChartLine.color = lineData.color
        val i = chartData.pointerTimeIndex
        val x = x(i)
        val y = y(i)
        canvas.drawCircle(x, y, chartLayoutParams.pointerCircleRadius, paints.paintPointerCircle)
        canvas.drawCircle(x, y, chartLayoutParams.pointerCircleRadius, paints.paintChartLine)
    }

    fun getPointerX(): Float {
        setVals()
        return x(chartData.pointerTimeIndex)
    }

    private inline fun calculatePoints() {
        setVals()
        var j = 0
        for (i in timeStart..timeEnd) {
            j = i * 2
            points[j] = x(i)
            points[j + 1] = y(i)
        }
    }

    private fun updatePathFromPoints() {
        with(path) {
            reset()
            if (drawingPoints.size > 1) {
                moveTo(drawingPoints[2 * timeStart], drawingPoints[2 * timeStart + 1])
                for (i in (2 * timeStart + 2) until 2 * timeEnd step 2) {
                    lineTo(drawingPoints[i], drawingPoints[i + 1])
                }
            }
        }
    }

    private inline fun x(index: Int) = kX * (chartData.time.values[index] - xmin)

    private inline fun y(index: Int) = mh - kY * (lineData.values[index] - chartData.valueMin)

    private inline fun setVals() {
        w = chartLayoutParams.w
        h = chartLayoutParams.h
        timeStart = chartData.timeIndexStart
        timeEnd = chartData.timeIndexEnd
        xmin = chartData.time.values[timeStart]
        mh = h - chartLayoutParams.paddingBottom
        kX = w / (chartData.time.values[timeEnd] - xmin)
        kY = 1f * (h - chartLayoutParams.paddingBottom - chartLayoutParams.paddingTop) / chartData.valueInterval
    }
}
