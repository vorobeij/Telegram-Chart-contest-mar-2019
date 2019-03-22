package au.sjowl.lib.view.telegramchart.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.params.ChartColors
import au.sjowl.lib.view.telegramchart.params.ChartLayoutParams
import au.sjowl.lib.view.telegramchart.params.ChartPaints

class ChartView : View {

    var chartData: ChartData = ChartData()
        set(value) {
            field = value
            charts.clear()
            axisY.chartData = value
            pointer.chartData = value
            value.columns.values.forEach { charts.add(Chart(it, layoutHelper, paints, value)) }
            chartData.scaleInProgress = false
        }

    private val charts = arrayListOf<Chart>()

    private val layoutHelper = ChartLayoutParams(context)

    private var paints = ChartPaints(context, ChartColors(context))

    private val pointer = ChartPointerPopup(context, paints)

    private val axisY = AxisY(layoutHelper, paints, chartData)

    private var onDrawPointer: ((x: Float, measuredWidth: Int) -> Unit) = pointer::updatePoints

    private var drawPointer = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutHelper.w = measuredWidth * 1f
        layoutHelper.h = measuredHeight * 1f
        onTimeIntervalChanged()
    }

    override fun onDraw(canvas: Canvas) {
        axisY.drawGrid(canvas)
        val activeCharts = charts
        activeCharts.forEach { it.draw(canvas) }
        axisY.drawMarks(canvas)
        if (drawPointer) {
            paints.paintGrid.alpha = 255
            canvas.drawLine(chartData.pointerTimeX, layoutHelper.h, chartData.pointerTimeX, layoutHelper.paddingTop.toFloat(), paints.paintGrid)
            activeCharts.forEach { it.drawPointer(canvas) }
            pointer.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                drawPointer = true
                updateTimeIndexFromX(event.x)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                drawPointer = false
                updateTimeIndexFromX(event.x)
                invalidate()
            }
        }
        return true
    }

    fun onTimeIntervalChanged() {
        adjustValueRange()
        axisY.onAnimateValues(0f)
        charts.forEach { it.updatePoints() }
        invalidate()
    }

    fun updateTheme() {
        paints = ChartPaints(context, ChartColors(context))
        axisY.paints = paints
        pointer.paints = paints
        charts.forEach { it.paints = paints }
        invalidate()
    }

    fun updateFinishState() {
        adjustValueRange()
        axisY.onAnimateValues(0f)
        charts.forEach { it.updateFinishState() }
    }

    fun updateStartPoints() {
        axisY.updateStartPoints()
        charts.forEach { it.updateStartPoints() }
    }

    fun onAnimateValues(v: Float) {
        axisY.onAnimateValues(v)
        charts.forEach { it.onAnimateValues(v) }
        invalidate()
    }

    private fun adjustValueRange() {
        val columns = chartData.columns.values
        columns.forEach { it.calculateBorders(chartData.timeIndexStart, chartData.timeIndexEnd) }
        val enabled = columns.filter { it.enabled }
        val chartsMin = enabled.minBy { it.min }?.min ?: 0
        val chartsMax = enabled.maxBy { it.max }?.max ?: 100
        axisY.adjustValuesRange(chartsMin, chartsMax)
    }

    private inline fun updateTimeIndexFromX(x: Float) {
        val t = chartData.pointerTimeIndex

        if (charts.size > 0) {
            chartData.pointerTimeIndex = chartData.timeIndexStart + (chartData.timeIntervalIndexes * x / measuredWidth).toInt()
            chartData.pointerTimeX = charts[0].getPointerX()
        }

        if (t != chartData.pointerTimeIndex) {
            onDrawPointer.invoke(x, measuredWidth)
            invalidate()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}