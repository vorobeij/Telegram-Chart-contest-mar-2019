package au.sjowl.lib.view.app.charts

import android.view.ViewGroup
import au.sjowl.apps.telegram.chart.R
import au.sjowl.lib.view.telegramchart.data.ChartData
import au.sjowl.lib.view.telegramchart.recycler.BaseRecyclerViewAdapter
import au.sjowl.lib.view.telegramchart.recycler.BaseViewHolder

class ChartAdapter : BaseRecyclerViewAdapter<ChartData, BaseViewHolder>() {

    override fun getViewHolderLayoutId(viewType: Int): Int = R.layout.rv_item_chart

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ChartViewHolder(inflate(parent, viewType))
    }
}