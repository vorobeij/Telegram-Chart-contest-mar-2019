package au.sjowl.lib.view.app

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import au.sjowl.apps.telegram.chart.R
import au.sjowl.lib.view.app.charts.ChartAdapter
import au.sjowl.lib.view.telegramchart.getColorFromAttr
import au.sjowl.lib.view.utils.ResourcesUtils
import kotlinx.android.synthetic.main.fr_charts.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChartsFragment : BaseFragment() {
    override val layoutId: Int get() = R.layout.fr_charts

    private var theme: Int = Themes.LIGHT

    private val chartsAdapter = ChartAdapter().apply {
        val json = ResourcesUtils.getResourceAsString("chart_data.json")
        val data = ChartColumnJsonParser(json).parse()
        items = data
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chartsRecyclerView.layoutManager = LinearLayoutManager(context)
        chartsRecyclerView.adapter = chartsAdapter

        menuTheme.onClick {
            theme = Themes.toggleTheme(theme)

            activity?.setTheme(Themes.styleFromTheme(theme))

            val pos = (chartsRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            chartsRecyclerView.adapter = chartsAdapter
            chartsRecyclerView.scrollToPosition(pos)

            toolbar.backgroundColor = context!!.getColorFromAttr(R.attr.colorToolbar)
            root.backgroundColor = context!!.getColorFromAttr(R.attr.colorWindow)

            this@ChartsFragment.view?.invalidate()
        }
    }
}