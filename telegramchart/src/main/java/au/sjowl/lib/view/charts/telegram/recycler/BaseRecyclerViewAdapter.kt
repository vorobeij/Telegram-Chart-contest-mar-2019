package au.sjowl.lib.view.charts.telegram.recycler

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * V - ViewHolder class
 * D - model data class
 */
abstract class BaseRecyclerViewAdapter<D : Any, V : BaseViewHolder> : RecyclerView.Adapter<V>() {

    open val diffutilsCallback = BaseDiffutilCallback<D>()

    open var items: List<D> = ArrayList()
        set(value) {
            diffutilsCallback.oldList = field
            diffutilsCallback.newList = value
            val diffRes = DiffUtil.calculateDiff(diffutilsCallback, true)
            field = value
            diffRes.dispatchUpdatesTo(this)
        }

    abstract fun getViewHolderLayoutId(viewType: Int): Int

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: V, position: Int) {
        (holder as BaseViewHolder).bind(items[position])
    }

    override fun onBindViewHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            val bundle = payloads[0] as Bundle
            (holder as BaseViewHolder).bind(items[position], bundle)
        }
    }

    fun inflate(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(parent.context).inflate(getViewHolderLayoutId(viewType), parent, false)
    }
}