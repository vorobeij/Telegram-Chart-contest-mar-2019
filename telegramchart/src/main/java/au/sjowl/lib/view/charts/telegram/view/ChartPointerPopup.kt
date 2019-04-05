package au.sjowl.lib.view.charts.telegram.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import au.sjowl.lib.view.charts.telegram.DateFormatter
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.getTextBounds
import au.sjowl.lib.view.charts.telegram.params.ChartPaints
import org.jetbrains.anko.dip

class ChartPointerPopup(
    context: Context,
    var paints: ChartPaints
) {

    var chartData = ChartData()

    private var title = ""

    private var items = listOf<ChartPoint>()

    private var rectRadius = context.dip(10).toFloat()

    private var timeIndex = 0

    private var w = 0f

    private var h = 0f

    private var x = 0f

    private val r1 = Rect()

    private val r2 = Rect()

    private var pad = context.dip(10).toFloat()

    private val valueFormatter = ValueFormatter()

    private var mw = 0

    fun updatePoints(x: Float, measuredWidth: Int) {
        this.mw = measuredWidth
        val tIndex = chartData.pointerTimeIndex
        if (timeIndex != tIndex) {
            timeIndex = tIndex

            val time = chartData.time.values[timeIndex]
            title = DateFormatter.formatLong(time)
            items = chartData.columns.values.filter { it.enabled }
                .map { ChartPoint(it.name, valueFormatter.format(it.values[timeIndex]), it.color) }

            measure()
            this.x = x - w / 2
            restrictX()
        }
    }

    fun draw(canvas: Canvas) {
        canvas.drawRoundRect(x, pad, x + w, h + pad, rectRadius, rectRadius, paints.paintPointerBackground)

        paints.paintPointerTitle.getTextBounds(title, r1)
        var x = x + pad
        var y0 = 2 * pad + r1.height()
        canvas.drawText(title, x, y0, paints.paintPointerTitle)

        y0 += pad
        var y: Float
        items.forEach {
            paints.paintPointerValue.color = it.color
            paints.paintPointerName.color = it.color
            paints.paintPointerValue.getTextBounds(it.value, r1)
            paints.paintPointerName.getTextBounds(it.chartName, r2)

            y = y0 + r1.height()
            canvas.drawText(it.value, x, y, paints.paintPointerValue)
            y += pad / 2 + r2.height()
            canvas.drawText(it.chartName, x, y, paints.paintPointerName)
            x += Math.max(r1.width(), r1.height()) + 2 * pad
        }
    }

    private fun restrictX() {
        if (x < pad) x = pad
        val max = mw - w - pad
        if (x > max) x = max
    }

    private fun measure() {
        w = Math.max(items.size * 2, 2) * pad + Math.max(
            paints.paintPointerTitle.measureText(title),
            items.map {
                Math.max(
                    paints.paintPointerValue.measureText(it.value),
                    paints.paintPointerName.measureText(it.chartName)
                )
            }.sum()
        )
        h = pad * 2
        paints.paintPointerTitle.getTextBounds(title, r1)
        h += r1.height()
        if (items.isNotEmpty()) {
            paints.paintPointerValue.getTextBounds(items[0].value, r1)
            paints.paintPointerName.getTextBounds(items[0].chartName, r2)
            h += pad + r1.height() + pad / 2 + r2.height()
        }
    }
}

private data class ChartPoint(
    val chartName: String,
    val value: String,
    val color: Int
)