package au.sjowl.lib.view.charts.telegram.recycler

import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DiffUtil

class BaseDiffutilCallback<D>(
    var oldList: List<D> = emptyList(),
    var newList: List<D> = emptyList(),
    var areItemsTheSame: ((oldItem: D, newItem: D) -> Boolean) = { oldItem, newItem -> oldItem == newItem },
    var areContentsTheSame: ((oldItem: D, newItem: D) -> Boolean) = { oldItem, newItem -> oldItem == newItem },
    var getChangePayload: ((oldItem: D, newItem: D) -> Any?) = { oldItem, newItem -> bundleOf() }
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areItemsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return areContentsTheSame(oldList[oldItemPosition], newList[newItemPosition])
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return getChangePayload(oldList[oldItemPosition], newList[newItemPosition])
    }
}