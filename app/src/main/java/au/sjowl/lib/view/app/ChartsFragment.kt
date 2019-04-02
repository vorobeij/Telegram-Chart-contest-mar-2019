package au.sjowl.lib.view.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import au.sjowl.apps.telegram.chart.R
import au.sjowl.lib.view.app.charts.Themes
import au.sjowl.lib.view.telegramchart.ChartContainer
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.getColorFromAttr
import au.sjowl.lib.view.utils.ResourcesUtils
import kotlinx.android.synthetic.main.fr_charts.*
import kotlinx.android.synthetic.main.rv_item_chart.view.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChartsFragment : BaseFragment() {

    override val layoutId: Int get() = R.layout.fr_charts

    private var theme: Int = Themes.LIGHT

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData().forEach { chartData ->
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
        val json = ResourcesUtils.getResourceAsString("chart_data.json")
        return ChartColumnJsonParser(json).parse()
    }
}