package au.sjowl.lib.view.charts.telegram

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.system.measureNanoTime

inline fun View.contains(px: Int, py: Int): Boolean {
    return px > x && px < x + measuredWidth && py > y && py < y + measuredHeight
}

inline fun View.contains(px: Float, py: Float): Boolean {
    return px > x && px < x + width && py > y && py < y + height
}

inline fun View.contains(event: MotionEvent): Boolean {
    return contains(event.x + x, event.y + y)
}

fun Context.getColorFromAttr(attrId: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

inline fun Paint.getTextBounds(text: String, rect: Rect) = getTextBounds(text, 0, text.length, rect)

fun measureDrawingMs(msg: String, block: (() -> Unit)) {
    val t = measureNanoTime {
        block.invoke()
    }
    println("$msg draw %.3f".format(t / 1000000f))
}