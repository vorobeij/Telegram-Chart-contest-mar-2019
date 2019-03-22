package au.sjowl.lib.view.app.charts

import android.view.View
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.recycler.BaseViewHolder
import kotlinx.android.synthetic.main.rv_item_chart.view.*

class ChartViewHolder(
    view: View
) : BaseViewHolder(view) {

    override fun bind(item: Any) {
        (item as ChartData)
        with(itemView) {
            item.initTimeWindow()
            chartContainer.updateTheme()
            chartContainer.chartData = item
        }
    }
}