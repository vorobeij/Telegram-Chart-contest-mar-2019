package au.sjowl.lib.view.charts.telegram.params

import android.content.Context
import org.jetbrains.anko.dip

class ChartLayoutParams(context: Context) {

    var w = 0f

    var h = 0f

    val paddingBottom = 2

    val paddingTop = context.dip(20)

    val paddingTextBottom = context.dip(6)

    val pointerCircleRadius = 20f

    val yMarks = 5
}