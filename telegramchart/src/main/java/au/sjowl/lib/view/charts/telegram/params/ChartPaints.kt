package au.sjowl.lib.view.charts.telegram.params

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import org.jetbrains.anko.sp

class ChartPaints(
    context: Context,
    val colors: ChartColors
) {
    val fontFamily: Typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    val paintGrid = paint().apply {
        color = colors.colorGrid
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    val paintChartText = paint().apply {
        color = colors.colorChartText
        textSize = context.sp(12f) * 1f
    }

    val paintChartLine = paint().apply {
        strokeWidth = 6f
        style = Paint.Style.STROKE
    }

    val paintPointerCircle = paint().apply {
        style = Paint.Style.FILL
        color = colors.colorPointer
    }

    val paintOverviewLine = paint().apply {
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    val paintOverviewWindowVerticals = paint().apply {
        strokeWidth = 12f
        style = Paint.Style.STROKE
        color = colors.colorOverviewWindow
    }

    val paintOverviewWindowHorizontals = paint().apply {
        strokeWidth = 3f
        style = Paint.Style.STROKE
        color = colors.colorOverviewWindow
    }

    val paintOverviewWindowTint = paint().apply {
        style = Paint.Style.FILL
        color = colors.colorOverviewTint
    }

    val paintPointerBackground = paint().apply {
        color = colors.colorBackground
        setShadowLayer(5f, 0f, 2f, Color.parseColor("#33000000"))
    }

    val paintMarksBackground = paint().apply {
        color = colors.colorBackground
    }

    val paintPointerTitle = paint().apply {
        typeface = fontFamily
        color = colors.colorText
        textSize = context.sp(12).toFloat()
    }

    val paintPointerValue = paint().apply {
        typeface = fontFamily
        textSize = context.sp(14).toFloat()
    }

    val paintPointerName = paint().apply {
        textSize = context.sp(14).toFloat()
    }

    private fun paint() = Paint().apply { isAntiAlias = true }
}