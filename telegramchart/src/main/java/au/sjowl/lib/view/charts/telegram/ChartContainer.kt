package au.sjowl.lib.view.charts.telegram

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.view.forEach
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.names.ChartItem
import au.sjowl.lib.view.charts.telegram.names.ChartItemHolderListener
import au.sjowl.lib.view.charts.telegram.names.ChartNameView
import au.sjowl.lib.view.charts.telegram.names.DividerView
import au.sjowl.lib.view.charts.telegram.params.ChartColors
import kotlinx.android.synthetic.main.chart_layout.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.layoutInflater

class ChartContainer : LinearLayout {

    var chartData: ChartData = ChartData()
        set(value) {
            field = value
            value.columns.values.forEach { it.calculateExtremums() }
            titleTextView.text = value.title

            chartOverview.chartData = chartData
            chartView.chartData = chartData
            axisTime.chartData = chartData

            chartNames.removeAllViews()
            var i = 0
            val max = value.columns.values.size - 1
            value.columns.values.forEach {
                chartNames.addView(ChartNameView(context).apply {
                    bind(ChartItem(it.id, it.name, it.color, it.enabled),
                        object : ChartItemHolderListener {
                            override fun onChecked(data: ChartItem, checked: Boolean) {
                                onAnimate(floatValueAnimator) {
                                    chartData.columns[data.chartId]!!.enabled = checked
                                }
                            }
                        }
                    )
                })
                if (i != max) chartNames.addView(DividerView(context).apply {
                    updateTheme(colors)
                })
                i++
            }

            requestLayout()
        }

    private var colors = ChartColors(context)

    private var animValue = 1f

    private val floatValueAnimator = ValueAnimator().apply {
        setFloatValues(1f, 0f)
        duration = 120
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            val v = animatedValue as Float
            if (v != animValue) {
                animValue = v
                chartOverview.onAnimateValues(v)
                chartView.onAnimateValues(v)
            }
        }
    }

    override fun onDetachedFromWindow() {
        floatValueAnimator.cancel()
        floatValueAnimator.removeAllUpdateListeners()
        super.onDetachedFromWindow()
    }

    fun updateTheme() {
        colors = ChartColors(context)
        backgroundColor = colors.colorBackground
        chartView.updateTheme(colors)
        axisTime.updateTheme(colors)
        chartOverview.updateTheme(colors)
        chartNames.forEach { (it as ThemedView).updateTheme(colors) }
    }

    fun init(context: Context, attrs: AttributeSet?) {
        context.layoutInflater.inflate(R.layout.chart_layout, this)
        chartOverview.onTimeIntervalChanged = {
            chartView.onTimeIntervalChanged()
            axisTime.onTimeIntervalChanged()
        }
    }

    private fun onAnimate(animator: ValueAnimator, block: () -> Unit) {
        chartOverview.updateStartPoints()
        chartView.updateStartPoints()

        block.invoke()

        chartOverview.updateFinishState()
        chartView.updateFinishState()

        animator.start()
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }
}