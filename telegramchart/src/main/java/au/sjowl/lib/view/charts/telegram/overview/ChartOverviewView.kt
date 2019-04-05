package au.sjowl.lib.view.charts.telegram.overview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import au.sjowl.lib.view.charts.telegram.BaseSurfaceView
import au.sjowl.lib.view.charts.telegram.ThemedView
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.params.ChartColors
import au.sjowl.lib.view.charts.telegram.params.ChartPaints
import org.jetbrains.anko.dip

class ChartOverviewView : BaseSurfaceView, ThemedView {

    var chartData: ChartData = ChartData()
        set(value) {
            field = value
            value.columns.values.forEach { chartColumn ->
                chartColumn.calculateBorders()
                value.columns.values.forEach { charts.add(OverviewChart(it, layoutHelper, paints, value)) }
                setChartsRange()
            }
        }

    var onTimeIntervalChanged: (() -> Unit) = {}

    private val touchHelper = TouchHelper()

    private val layoutHelper = OverviewLayoutParams(context)

    private val rectangles = OverviewRectangles(context.dip(10))

    private val charts = arrayListOf<OverviewChart>()

    private var paints = ChartPaints(context, ChartColors(context))

    private var chartsBmp: Bitmap? = null

    private val chartsCanvas = Canvas()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectangles.reset(0f, 0f, measuredWidth * 1f, measuredHeight * 1f)

        layoutHelper.h = measuredHeight.toFloat()
        layoutHelper.w = measuredWidth.toFloat()

        charts.forEach { it.setupPoints() }
        createChartsBitmap()
        invalidateChartsBitmap()
    }

    // todo scale with 2 pointers
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rectangles.updateTouch()
                touchHelper.touchMode = rectangles.getTouchMode(event.x, event.y)

                touchHelper.xDown = event.x
                touchHelper.timeStartDownIndex = chartData.timeIndexStart
                touchHelper.timeEndDownIndex = chartData.timeIndexEnd
            }
            MotionEvent.ACTION_MOVE -> {
                val delta = touchHelper.xDown - event.x
                val deltaIndex = -canvasToIndexInterval(delta.toInt())
                when (touchHelper.touchMode) {
                    TOUCH_NONE -> {
                    }
                    TOUCH_DRAG -> {
                        val w = chartData.timeIndexEnd - chartData.timeIndexStart
                        val s = touchHelper.timeStartDownIndex + deltaIndex
                        val e = touchHelper.timeEndDownIndex + deltaIndex

                        when {
                            s < 0 -> {
                                chartData.timeIndexStart = 0
                                chartData.timeIndexEnd = chartData.timeIndexStart + w
                            }
                            e >= chartData.time.values.size -> {
                                chartData.timeIndexEnd = chartData.time.values.size - 1
                                chartData.timeIndexStart = chartData.timeIndexEnd - w
                            }
                            else -> {
                                chartData.timeIndexEnd = e
                                chartData.timeIndexStart = s
                            }
                        }
                        chartData.scaleInProgress = true
                        onTimeIntervalChanged()
                        invalidate()
                    }
                    TOUCH_SCALE_LEFT -> {
                        chartData.timeIndexStart = Math.min(touchHelper.timeStartDownIndex + deltaIndex, chartData.timeIndexEnd - SCALE_THRESHOLD)
                        chartData.timeIndexStart = if (chartData.timeIndexStart < 0) 0 else chartData.timeIndexStart
                        chartData.scaleInProgress = true
                        onTimeIntervalChanged()
                        invalidate()
                    }
                    TOUCH_SCALE_RIGHT -> {
                        chartData.timeIndexEnd = Math.max(touchHelper.timeEndDownIndex + deltaIndex, chartData.timeIndexStart + SCALE_THRESHOLD)
                        chartData.timeIndexEnd = if (chartData.timeIndexEnd >= chartData.time.values.size) chartData.time.values.size - 1 else chartData.timeIndexEnd
                        chartData.scaleInProgress = true
                        onTimeIntervalChanged()
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                if (touchHelper.touchMode != TOUCH_NONE) {
                    touchHelper.touchMode = TOUCH_NONE
                    chartData.scaleInProgress = false
                    onTimeIntervalChanged()
                }
            }
        }
        return true
    }

    override fun drawSurface(canvas: Canvas) {
        drawCharts(canvas)
        drawBackground(canvas)
        drawWindow(canvas)
    }

    override fun updateTheme(colors: ChartColors) {
        paints = ChartPaints(context, colors)
        invalidate()
    }

    fun updateStartPoints() {
        charts.forEach { it.updateStartPoints() }
    }

    fun onAnimateValues(v: Float) {
        charts.forEach { it.onAnimateValues(v) }
        invalidate()
    }

    fun updateFinishState() {
        setChartsRange()
        charts.forEach { it.updateFinishState() }
    }

    private fun drawCharts(canvas: Canvas) {
        canvas.drawColor(paints.colors.colorBackground)
        charts.forEach { it.draw(canvas) }
    }

    private fun createChartsBitmap() {
        if (measuredHeight > 0 && measuredWidth > 0) {
            chartsBmp?.recycle()
            chartsBmp = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_4444)
            chartsCanvas.setBitmap(chartsBmp)
        }
    }

    private fun invalidateChartsBitmap() {
        if (measuredHeight > 0 && measuredWidth > 0) {
            println("redraw charts")
            charts.forEach { it.draw(chartsCanvas) }
        }
    }

    private fun setChartsRange() {
        var min = Int.MAX_VALUE
        var max = Int.MIN_VALUE
        chartData.columns.values.filter { it.enabled }.forEach { chart ->
            if (chart.min < min) min = chart.chartMin
            if (chart.max > max) max = chart.chartMax
        }

        charts.forEach {
            it.min = min
            it.max = max
        }
    }

    private fun drawWindow(canvas: Canvas) {
        with(rectangles.rectTimeWindow) {
            left = timeToCanvas(chartData.timeIndexStart)
            right = timeToCanvas(chartData.timeIndexEnd)
        }
        with(canvas) {
            drawLine(rectangles.rectTimeWindow.left, 0f, rectangles.rectTimeWindow.left, measuredHeight * 1f, paints.paintOverviewWindowVerticals)
            drawLine(rectangles.rectTimeWindow.right, 0f, rectangles.rectTimeWindow.right, measuredHeight * 1f, paints.paintOverviewWindowVerticals)
            drawLine(rectangles.rectTimeWindow.left, 0f, rectangles.rectTimeWindow.right, 0f, paints.paintOverviewWindowHorizontals)
            drawLine(rectangles.rectTimeWindow.left, measuredHeight * 1f, rectangles.rectTimeWindow.right, measuredHeight * 1f, paints.paintOverviewWindowHorizontals)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        rectangles.rectBgLeft.right = timeToCanvas(chartData.timeIndexStart)
        rectangles.rectBgRight.left = timeToCanvas(chartData.timeIndexEnd)
        canvas.drawRect(rectangles.rectBgLeft, paints.paintOverviewWindowTint)
        canvas.drawRect(rectangles.rectBgRight, paints.paintOverviewWindowTint)
    }

    private inline fun timeToCanvas(timeIndex: Int): Float = measuredWidth * 1f * timeIndex / chartData.time.values.size

    private inline fun canvasToIndexInterval(canvasDistance: Int): Int = canvasDistance * chartData.time.values.size / measuredWidth

    companion object {
        const val TOUCH_NONE = -1
        const val TOUCH_DRAG = 0
        const val TOUCH_SCALE_LEFT = 1
        const val TOUCH_SCALE_RIGHT = 2
        const val SCALE_THRESHOLD = 14
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
}