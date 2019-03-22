package au.sjowl.lib.view.telegramchart.params

import android.content.Context
import au.sjowl.lib.view.telegramchart.R
import au.sjowl.lib.view.telegramchart.getColorFromAttr

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