package au.sjowl.lib.view.charts.telegram.params

import android.content.Context
import au.sjowl.lib.view.charts.telegram.R
import au.sjowl.lib.view.charts.telegram.getColorFromAttr

class ChartColors(
    context: Context
) {
    val colorBackground = context.getColorFromAttr(R.attr.colorBackground)
    val colorPointer = context.getColorFromAttr(R.attr.colorPointer)
    val colorGrid = context.getColorFromAttr(R.attr.colorGrid)
    val colorOverviewWindow = context.getColorFromAttr(R.attr.colorOverviewWindow)
    val colorOverviewTint = context.getColorFromAttr(R.attr.colorOverviewTint)
    val colorChartText = context.getColorFromAttr(R.attr.colorChartText)
    val colorText = context.getColorFromAttr(R.attr.colorText)
}