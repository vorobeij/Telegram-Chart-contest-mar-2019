package au.sjowl.lib.view.telegramchart.names

import android.view.ViewGroup
import au.sjowl.lib.view.telegramchart.R
import au.sjowl.lib.view.telegramchart.recycler.BaseRecyclerViewAdapter
import au.sjowl.lib.view.telegramchart.recycler.BaseViewHolder

class ChartItemAdapter(
    private val onItemClickListener: ChartItemHolderListener
) : BaseRecyclerViewAdapter<ChartItem, BaseViewHolder>() {

    override fun getViewHolderLayoutId(viewType: Int): Int = R.layout.rv_item_chart_title

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ChartItemViewHolder(inflate(parent, viewType), onItemClickListener)
    }
}