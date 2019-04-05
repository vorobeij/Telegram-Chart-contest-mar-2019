package au.sjowl.lib.view.charts.telegram.overview

import android.graphics.Canvas
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.data.ChartLineData
import au.sjowl.lib.view.charts.telegram.params.ChartPaints

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

    private var points = FloatArray(lineData.values.size * 2)

    private var pointsFrom = FloatArray(lineData.values.size * 2)

    private var drawingPoints = FloatArray(lineData.values.size * 2)

    private val pointsPerDip = 0.5f

    private var numberOfPointsToDraw = layoutHelper.w / layoutHelper.dip * pointsPerDip

    private var xmin = 0L

    private var h = 0f

    private var mh = 0f

    private var kX = 0f

    private var kY = 0f

    private var animValue = 0f

    private var truncatedSize = 0

    fun setupPoints() {
        numberOfPointsToDraw = layoutHelper.w / layoutHelper.dip * pointsPerDip
        points = FloatArray((numberOfPointsToDraw * 2).toInt())
        pointsFrom = FloatArray((numberOfPointsToDraw * 2).toInt())
        drawingPoints = FloatArray((numberOfPointsToDraw * 2).toInt())

        setVals()
        val t = chartData.time.values
        val column = lineData.values
        var j = 0
        val step = step()
        for (i in 0 until t.size - step step step) {
            points[j++] = kX * (t[i] - xmin)
            points[j++] = mh - kY * (column[i] - min)
        }
        truncatedSize = j
        points.copyInto(drawingPoints)
    }

    fun updateStartPoints() {
        drawingPoints.copyInto(pointsFrom)
        enabled = lineData.enabled
    }

    fun updateFinishState() {
        setVals()
        val column = lineData.values
        var j = 1
        val step = step()
        for (i in 0 until column.size - step step step) {
            points[j] = mh - kY * (column[i] - min)
            j += 2
        }
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
            drawingPoints[i + 1] = points[i + 1] + (pointsFrom[i + 1] - points[i + 1]) * animValue
        }
    }

    fun draw(canvas: Canvas) {
        if (lineData.enabled || enabled) {
            val paint = paints.paintOverviewLine
            paint.color = lineData.color
            paint.alpha = (alpha * 255).toInt()
            canvas.drawLines(drawingPoints, 0, truncatedSize, paint)
            canvas.drawLines(drawingPoints, 2, truncatedSize - 2, paint)
        }
    }

    private fun step(): Int {
        return Math.ceil((1f * chartData.time.values.size / numberOfPointsToDraw).toDouble()).toInt()
    }

    private inline fun setVals() {
        xmin = chartData.time.min
        h = 1f * (layoutHelper.h - layoutHelper.paddingBottom - layoutHelper.paddingTop)
        mh = layoutHelper.h * 1f - layoutHelper.paddingBottom
        kX = layoutHelper.w * 1f / chartData.time.interval
        kY = h / (max - min)
    }
}