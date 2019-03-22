package au.sjowl.lib.view.telegramchart.overview

import android.graphics.Canvas
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.data.ChartLineData
import au.sjowl.lib.view.telegramchart.params.ChartPaints

class OverviewChart(
    val lineData: ChartLineData,
    val layoutHelper: OverviewLayoutParams,
    var paints: ChartPaints,
    val chartData: ChartData
) {

    var min = 0

    var max = 0

    protected var enabled = lineData.enabled

    protected var alpha = 1f

    private val points = FloatArray(lineData.values.size * 2)

    private val pointsFrom = FloatArray(lineData.values.size * 2)

    private val drawingPoints = FloatArray(lineData.values.size * 2)

    private val drawingPointsOdd = FloatArray(lineData.values.size * 2 - 2)

    private var xmin = 0L

    private var h = 0f

    private var mh = 0f

    private var kX = 0f

    private var kY = 0f

    private var animValue = 0f

    fun updatePoints() {
        setVals()
        val t = chartData.time.values
        val column = lineData.values
        var j = 0
        for (i in 0 until column.size) {
            j = i * 2
            points[j] = kX * (t[i] - xmin)
            points[j + 1] = mh - kY * (column[i] - min)
        }
        points.copyInto(drawingPoints)
    }

    fun updateStartPoints() {
        drawingPoints.copyInto(pointsFrom)
        enabled = lineData.enabled
    }

    fun updateFinishState() {
        updatePoints()
    }

    fun onAnimateValues(v: Float) {
        alpha = when {
            lineData.enabled && enabled -> 1f
            lineData.enabled && !enabled -> 1f - v
            !lineData.enabled && !enabled -> 0f
            else -> v
        }
        animValue = v
        for (i in 0 until (points.size - 1) step 2) {
            drawingPoints[i] = points[i]
            drawingPoints[i + 1] = points[i + 1] + (pointsFrom[i + 1] - points[i + 1]) * animValue
        }
    }

    fun draw(canvas: Canvas) {
        if (lineData.enabled || enabled) {
            updatePathFromPoints()
            val paint = paints.paintOverviewLine
            paint.color = lineData.color
            paint.alpha = (alpha * 255).toInt()
            canvas.drawLines(drawingPoints, paint)
            canvas.drawLines(drawingPointsOdd, paint)
        }
    }

    private fun updatePathFromPoints() {
        drawingPoints.copyInto(drawingPointsOdd, 0, 2)
    }

    private inline fun setVals() {
        xmin = chartData.time.min
        h = 1f * (layoutHelper.h - layoutHelper.paddingBottom - layoutHelper.paddingTop)
        mh = layoutHelper.h * 1f - layoutHelper.paddingBottom
        kX = layoutHelper.w * 1f / chartData.time.interval
        kY = h / (max - min)
    }
}