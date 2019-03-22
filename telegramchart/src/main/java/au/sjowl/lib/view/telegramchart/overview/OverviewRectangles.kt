package au.sjowl.lib.view.telegramchart.overview

import android.graphics.RectF

internal class OverviewRectangles(
    private val touchWidth: Int
) {

    val rectTimeWindow = RectF()

    val rectBgLeft = RectF()

    val rectBgRight = RectF()

    private val rectBorderLeft = RectF()

    private val rectBorderRight = RectF()

    fun reset(left: Float, top: Float, right: Float, bottom: Float) {
        rectTimeWindow.top = top
        rectBgLeft.top = top
        rectBgRight.top = top
        rectBorderLeft.top = top
        rectBorderRight.top = top

        rectTimeWindow.bottom = bottom
        rectBgLeft.bottom = bottom
        rectBgRight.bottom = bottom
        rectBorderLeft.bottom = bottom
        rectBorderRight.bottom = bottom

        rectBgLeft.left = left
        rectBgRight.right = right
    }

    fun updateTouch() {
        rectBorderLeft.left = rectTimeWindow.left - touchWidth
        rectBorderLeft.right = rectTimeWindow.left + touchWidth

        rectBorderRight.left = rectTimeWindow.right - touchWidth
        rectBorderRight.right = rectTimeWindow.right + touchWidth
    }

    fun getTouchMode(x: Float, y: Float): Int {
        return when {
            rectBorderLeft.contains(x, y) -> ChartOverviewView.TOUCH_SCALE_LEFT
            rectBorderRight.contains(x, y) -> ChartOverviewView.TOUCH_SCALE_RIGHT
            rectTimeWindow.contains(x, y) -> ChartOverviewView.TOUCH_DRAG
            else -> ChartOverviewView.TOUCH_NONE
        }
    }
}