package au.sjowl.lib.view.telegramchart

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.names.ChartItem
import au.sjowl.lib.view.telegramchart.names.ChartItemAdapter
import au.sjowl.lib.view.telegramchart.names.ChartItemHolderListener
import au.sjowl.lib.view.telegramchart.names.MiddleDividerItemDecoration
import kotlinx.android.synthetic.main.chart_layout.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.layoutInflater

class ChartContainer : LinearLayout {

    var chartData: ChartData = ChartData()
        set(value) {
            field = value
            value.columns.values.forEach { it.calculateExtremums() }
            titleTextView.text = value.title
            onAnimate(floatValueAnimator) {
                chartOverview.chartData = chartData
                chartView.chartData = chartData
                axisTime.chartData = chartData
            }

            chartsAdapter.items = value.columns.values.map { ChartItem(it.id, it.name, it.color, it.enabled) }

            requestLayout()
        }

    private val floatValueAnimator = ValueAnimator().apply {
        setFloatValues(1f, 0f)
        duration = 120
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener {
            val v = animatedValue as Float
            chartOverview.onAnimateValues(v)
            chartView.onAnimateValues(v)
        }
    }

    private val chartsAdapter = ChartItemAdapter(object : ChartItemHolderListener {
        override fun onChecked(data: ChartItem, checked: Boolean) {
            onAnimate(floatValueAnimator) {
                chartData.columns[data.chartId]!!.enabled = checked
            }
        }
    })

    override fun onDetachedFromWindow() {
        floatValueAnimator.cancel()
        floatValueAnimator.removeAllUpdateListeners()
        super.onDetachedFromWindow()
    }

    fun updateTheme() {
        backgroundColor = context.getColorFromAttr(R.attr.colorBackground)
        chartView.updateTheme()
        axisTime.updateTheme()
        chartOverview.updateTheme()
        recyclerView.addItemDecoration(MiddleDividerItemDecoration(context))
        recyclerView.adapter = chartsAdapter
    }

    fun init(context: Context, attrs: AttributeSet?) {
        context.layoutInflater.inflate(R.layout.chart_layout, this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chartsAdapter
            addItemDecoration(MiddleDividerItemDecoration(context))
        }
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