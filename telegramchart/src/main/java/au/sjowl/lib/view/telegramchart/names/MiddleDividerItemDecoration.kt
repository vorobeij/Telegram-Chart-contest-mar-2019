package au.sjowl.lib.view.telegramchart.names

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import au.sjowl.lib.view.telegramchart.R
import org.jetbrains.anko.dip

class MiddleDividerItemDecoration(val context: Context) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.rv_divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = context.dip(68)
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}