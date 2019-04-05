package au.sjowl.lib.view.charts.telegram.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import au.sjowl.lib.view.charts.telegram.ChartContainer
import au.sjowl.lib.view.charts.telegram.R
import au.sjowl.lib.view.charts.telegram.data.ChartData
import au.sjowl.lib.view.charts.telegram.fragment.charts.Themes
import au.sjowl.lib.view.charts.telegram.getColorFromAttr
import kotlinx.android.synthetic.main.fr_charts.*
import kotlinx.android.synthetic.main.rv_item_chart.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChartsFragment : BaseFragment() {

    override val layoutId: Int get() = R.layout.fr_charts

    private val dataFile = "chart_data.json"

    private var theme: Int = Themes.LIGHT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData().forEach { chartData ->
            println("dots = ${4 * chartData.time.values.size}")
            chartData.initTimeWindow()
            val v = LayoutInflater.from(context).inflate(R.layout.rv_item_chart, chartsContainer, false)
            v.chartContainer.updateTheme()
            v.chartContainer.chartData = chartData
            chartsContainer.addView(v)
        }

        menuTheme.onClick {
            theme = Themes.toggleTheme(theme)

            activity?.setTheme(Themes.styleFromTheme(theme))

            chartsContainer.children.forEach { (it as ChartContainer).updateTheme() }

            toolbar.backgroundColor = context!!.getColorFromAttr(R.attr.colorToolbar)
            root.backgroundColor = context!!.getColorFromAttr(R.attr.colorWindow)

            this@ChartsFragment.view?.invalidate()
        }
    }

    private fun getData(): List<ChartData> {
        val json = ResourcesUtils.getResourceAsString(dataFile)
        return ChartColumnJsonParser(json).parse()
    }
}