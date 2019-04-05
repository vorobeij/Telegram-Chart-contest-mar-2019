package au.sjowl.lib.view.charts.telegram

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

abstract class BaseSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private var surfaceViewThread: SurfaceViewThread? = null

    private var hasSurface: Boolean = false

    private var dirty = true

    abstract fun drawSurface(canvas: Canvas)

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        hasSurface = true

        if (surfaceViewThread == null)
            surfaceViewThread = SurfaceViewThread()

        surfaceViewThread!!.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
        surfaceViewThread?.requestExitAndWait()
        surfaceViewThread = null
    }

    override fun invalidate() {
        dirty = true
        super.invalidate()
    }

    /**
     * Call from onPause() of host Fragment/Activity
     */
    fun stopThread() {
        surfaceViewThread?.requestExitAndWait()
        surfaceViewThread = null
    }

    protected fun restart() {
        surfaceViewThread?.requestExitAndWait()
        surfaceViewThread = SurfaceViewThread()
        hasSurface = true
        surfaceViewThread!!.start()
    }

    private fun init() {
        if (holder != null)
            holder.addCallback(this)

        hasSurface = false
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private inner class SurfaceViewThread : Thread() {

        private var running: Boolean = true
        private var t = 0L

        override fun run() {
            var canvas: Canvas? = null

            while (running) {
                val passed = System.currentTimeMillis() - t
                if (passed in 0..15) Thread.sleep(passed)

                if (dirty) {
                    dirty = false

                    try {
                        canvas = holder.lockCanvas()

                        synchronized(holder) {
                            t = System.currentTimeMillis()
                            drawSurface(canvas)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        if (canvas != null) {
                            holder.unlockCanvasAndPost(canvas)
                        }
                    }
                } else {
                    Thread.sleep(16)
                }
            }
        }

        fun requestExitAndWait() {
            running = false

            try {
                join()
            } catch (ignored: InterruptedException) {
            }
        }
    }
}