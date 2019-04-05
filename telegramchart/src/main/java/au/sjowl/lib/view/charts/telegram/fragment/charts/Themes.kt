package au.sjowl.lib.view.charts.telegram.fragment.charts

import au.sjowl.lib.view.charts.telegram.R

object Themes {
    const val LIGHT = 0
    const val DARK = 1

    fun toggleTheme(theme: Int): Int {
        return (theme + 1) % 2
    }

    fun styleFromTheme(theme: Int): Int {
        return when (theme) {
            LIGHT -> R.style.AppTheme_Light
            DARK -> R.style.AppTheme_Dark
            else -> throw IllegalStateException("No such theme $theme")
        }
    }
}