package au.sjowl.lib.view.charts.telegram.time

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import au.sjowl.lib.view.charts.telegram.ThemedView
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.params.ChartColors
import au.sjowl.lib.view.charts.telegram.params.ChartPaints
import java.util.LinkedList

class AxisTimeView : View, ThemedView {

    var chartData: ChartData = ChartData()

    var marks = 5

    private var paints: ChartPaints = ChartPaints(context, ChartColors(context))

    private val rectText = Rect()

    private val xTo = FloatArray(marks + 1)

    private val xFrom = FloatArray(marks + 1)

    private val scalablePoints = LinkedList<ScalablePoint>()

    private var defaultDistance = 1f

    private var halfText = 0

    private val floatValueAnimator = ValueAnimator().apply {
        setFloatValues(1f, 0f)
        duration = 120
        interpolator = AccelerateInterpolator()
        addUpdateListener {
            val v = animatedValue as Float

            for (i in 0..marks) {
                with(scalablePoints[i]) {
                    x = xTo[i] + (xTo[i] - xFrom[i]) * v
                    alpha = 1f - v
                }
            }
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        scalablePoints.clear()
        onScaleEnd()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val h = paddingTop + paddingBottom + rectText.height()
        setMeasuredDimension(widthMeasureSpec, h)
    }

    override fun onDraw(canvas: Canvas) {
        val y = (paddingTop + rectText.height()).toFloat()
        for (i in 0 until scalablePoints.size) {
            val t = scalablePoints[i]
            val title = t.title
            paints.paintChartText.alpha = (t.alpha * 255).toInt()
            canvas.drawText(title, 0, title.length, t.x * width - halfText, y, paints.paintChartText)
        }
    }

    override fun onDetachedFromWindow() {
        floatValueAnimator.cancel()
        floatValueAnimator.removeAllUpdateListeners()
        super.onDetachedFromWindow()
    }

    override fun updateTheme(colors: ChartColors) {
        paints = ChartPaints(context, colors)
        invalidate()
    }

    fun onTimeIntervalChanged() {
        if (chartData.scaleInProgress) {
            floatValueAnimator.cancel()
            onScale()
        } else if (!chartData.scaleInProgress) {
            onScaleEnd()
        }

        invalidate()
    }

    private fun onScale() {
        val t0 = measuredWidth / halfText

        val start: Long = chartData.timeInterval / t0 + chartData.timeStart
        val timeInterval = chartData.timeInterval - 2 * (start - chartData.timeStart)

        scalablePoints.forEach {
            it.x = 1f * (it.t - start) / timeInterval
        }
        addBorderPoints()
//        println("size = ${scalablePoints.size}, ${scalablePoints.map { it.x }}")
        val distanceScale = Math.abs(scalablePoints[1].x - scalablePoints[0].x) / defaultDistance
        when {
            distanceScale in 0f..0.5f -> {
                scalablePoints.removeAll(scalablePoints.filter { it.toFade })

                for (i in 0 until scalablePoints.size) {
                    scalablePoints[i].toFade = i % 2 == 0
                }
                val newDistanceScale = (scalablePoints[1].x - scalablePoints[0].x) / defaultDistance
                scalablePoints.forEach { p -> if (p.toFade) p.alpha = newDistanceScale }
            }
            distanceScale in 0.5f..1f -> {
                val a = 4f * distanceScale - 3f
                scalablePoints.forEach { p -> if (p.toFade) p.alpha = a }
            }
            distanceScale > 1.5f -> {
                scalablePoints.forEach { it.toFade = false }
                for (i in scalablePoints.size - 2 downTo 0) {
                    addPointBetween(i)
                }
            }
        }
        addBorderPoints()
        removeInvisiblePoints()
    }

    private fun removeInvisiblePoints() {
        val t0 = measuredWidth / halfText
        val h = 2 / t0
        var it: ScalablePoint
        for (i in scalablePoints.size - 1 downTo 0) {
            it = scalablePoints[i]
            if (!(it.x > -h && it.x < 1 + h)) {
                scalablePoints.remove(it)
            }
        }

        for (i in scalablePoints.size - 1 downTo 1) {
            if (scalablePoints[i].x - scalablePoints[i - 1].x < defaultDistance / 2) {
                scalablePoints.removeAt(i)
            }
        }
    }

    private fun onScaleEnd() {
        if (measuredWidth == 0) return
        val t0 = measuredWidth / halfText
        val start: Long = chartData.timeInterval / t0 + chartData.timeStart
        val timeInterval = chartData.timeInterval - 2 * (start - chartData.timeStart)
        val step = timeInterval / marks

        if (scalablePoints.isEmpty()) {
            for (i in 0..marks) {
                val t = step * i + start
                scalablePoints.add(ScalablePoint(
                    t = t,
                    xStart = 1f * (t - chartData.timeStart) / chartData.timeInterval,
                    interval = timeInterval,
                    start = start
                ))
            }

            defaultDistance = scalablePoints[1].x - scalablePoints[0].x

            for (i in 1 until scalablePoints.size step 2) {
                scalablePoints[i].toFade = true
            }
        } else {
            // cleanup points
            removeInvisiblePoints()
            while (scalablePoints.size > marks + 1) scalablePoints.removeLast()
            while (scalablePoints.size < marks + 1) scalablePoints.add(ScalablePoint(
                xStart = 1f,
                interval = timeInterval,
                start = start,
                toFade = false
            ))
            scalablePoints.forEach {
                it.toFade = false
                it.alpha = 1f
            }

            // set start and end points
            for (i in 0..marks) {
                val t = step * i + start
                scalablePoints[i].t = t
                xTo[i] = 1f * (t - chartData.timeStart) / chartData.timeInterval
                xFrom[i] = scalablePoints[i].x
            }
            // animate
            floatValueAnimator.cancel()
            floatValueAnimator.start()
        }
    }

    private fun addPointBetween(i: Int) {
        val f = scalablePoints[i]
        val dt = scalablePoints[i + 1].t - scalablePoints[i].t
        val dx = scalablePoints[i + 1].x - scalablePoints[i].x
        val dxStart = scalablePoints[i + 1].xStart - scalablePoints[i].xStart

        scalablePoints.add(i + 1, ScalablePoint(
            t = f.t + dt / 2,
            xStart = f.xStart + dxStart / 2,
            x = f.x + dx / 2,
            interval = f.interval,
            start = f.start,
            toFade = true,
            alpha = 0.01f
        ))
    }

    private fun addBorderPoints() {
        if (scalablePoints.size <= 1) {
            onScaleEnd()
        }
        while (scalablePoints[0].x in 0f..1f) {

            val f = scalablePoints[0]
            scalablePoints.add(0, ScalablePoint(
                t = f.t - (scalablePoints[1].t - scalablePoints[0].t),
                xStart = f.xStart - (scalablePoints[1].xStart - scalablePoints[0].xStart),
                x = f.x - (scalablePoints[1].x - scalablePoints[0].x),
                interval = f.interval,
                start = f.start,
                toFade = !f.toFade
            ))
        }

        while (scalablePoints.last.x in 0f..1f) {
            val l = scalablePoints.last()
            val i = scalablePoints.lastIndex
            scalablePoints.add(ScalablePoint(
                t = l.t + (scalablePoints[i].t - scalablePoints[i - 1].t),
                xStart = l.xStart + (scalablePoints[i].xStart - scalablePoints[i - 1].xStart),
                x = l.x + (scalablePoints[i].x - scalablePoints[i - 1].x),
                interval = l.interval,
                start = l.start,
                toFade = !l.toFade
            ))
        }
    }

    init {
        paints.paintChartText.getTextBounds("Mar 222", 0, 7, rectText)
        halfText = rectText.width() / 2
    }

    constructor (context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}