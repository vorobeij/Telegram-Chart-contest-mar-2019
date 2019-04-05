package au.sjowl.lib.view.charts.telegram

import au.sjowl.lib.view.charts.telegram.params.ChartColors

interface ThemedView {
    fun updateTheme(colors: ChartColors)
}