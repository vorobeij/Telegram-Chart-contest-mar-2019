package au.sjowl.lib.view.charts.telegram.names

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import au.sjowl.lib.view.charts.telegram.ThemedView
import au.sjowl.lib.view.charts.telegram.params.ChartColors
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent

class DividerView : View, ThemedView {

    override fun updateTheme(colors: ChartColors) {
        backgroundColor = colors.colorGrid
    }

    private fun init() {
        layoutParams = LinearLayout.LayoutParams(matchParent, dip(1)).apply {
            setMargins(dip(64), 0, 0, 0)
        }
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
}