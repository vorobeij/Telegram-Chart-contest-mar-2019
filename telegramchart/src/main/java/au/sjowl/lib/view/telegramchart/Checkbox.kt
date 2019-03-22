package au.sjowl.lib.view.telegramchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import org.jetbrains.anko.sdk27.coroutines.onClick

class Checkbox : ImageView {

    var checked: Boolean = false
        set(value) {
            field = value
            (drawable as AnimatedVectorDrawableCompat).stop()
            setImageDrawable(if (value) iconTick else iconPoint)
            (drawable as AnimatedVectorDrawableCompat).start()
        }

    @ColorInt
    var color: Int = Color.GREEN
        set(value) {
            field = value
            paintBackground.color = value
        }

    var radiusPercent: Float = 0.09f

    private var onCheckedChangedListener: ((checked: Boolean) -> Unit)? = null

    private val paintBackground = Paint().apply {
        isAntiAlias = true
        color = this@Checkbox.color
        style = Paint.Style.FILL
    }

    private val rectBackground = RectF()

    private val iconTick = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_tick_to_point) as AnimatedVectorDrawableCompat

    private val iconPoint = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_point_to_tick) as AnimatedVectorDrawableCompat

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rectBackground.bottom = h * 1f
        rectBackground.right = w * 1f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(rectBackground, rectBackground.width() * radiusPercent, rectBackground.height() * radiusPercent, paintBackground)
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        onCheckedChangedListener = null
        super.onDetachedFromWindow()
    }

    fun check() {
        checked = !checked
    }

    fun onCheckedChangedListener(listener: ((checked: Boolean) -> Unit)?) {
        onCheckedChangedListener = listener
    }

    private fun init() {
        onClick {
            checked = !checked
            onCheckedChangedListener?.invoke(checked)
        }
        setImageDrawable(iconTick)
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