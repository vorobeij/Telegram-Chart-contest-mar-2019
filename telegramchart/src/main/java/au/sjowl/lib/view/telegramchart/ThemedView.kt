package au.sjowl.lib.view.telegramchart

import au.sjowl.lib.view.telegramchart.params.ChartColors

interface ThemedView {
    fun updateTheme(colors: ChartColors)
}