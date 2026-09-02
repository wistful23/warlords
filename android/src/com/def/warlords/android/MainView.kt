package com.def.warlords.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.graphics.toRectF
import com.def.warlords.control.MainController
import com.def.warlords.control.common.Dimensions
import com.def.warlords.platform.Platform
import com.def.warlords.platform.PlatformHolder
import com.def.warlords.sound.Sound
import com.def.warlords.util.Logger
import com.def.warlords.util.Timer
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.InputStream

private const val OFFSET_TOP = 32

private const val MIN_PAINT_INTERVAL_MS = 30L

private const val PAINT_SCHEDULED = -1L

class MainView(private val context: Context) : View(context), Platform {
    private val thread = LooperThread()
    private val controller = MainController()

    private val dstRect: Rect
    private var backBuffer = Graphics(Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT)
    private var frontBuffer = Graphics(Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT)
    private var dosScreen: DosScreen? = null

    private var lastPaintTime = 0L

    private val paint = Runnable {
        lastPaintTime = SystemClock.uptimeMillis()
        controller.paint(backBuffer)
        // Swap the buffers.
        frontBuffer = backBuffer.also { backBuffer = frontBuffer }
        // Enqueue `View.onDraw()`.
        postInvalidate()
    }

    private val schedulePaint = Runnable {
        if (lastPaintTime == PAINT_SCHEDULED) {
            return@Runnable
        }
        val elapsed = SystemClock.uptimeMillis() - lastPaintTime
        if (elapsed >= MIN_PAINT_INTERVAL_MS) {
            paint.run()
        } else {
            lastPaintTime = PAINT_SCHEDULED
            thread.postDelayed(paint, (MIN_PAINT_INTERVAL_MS - elapsed).toInt())
        }
    }

    init {
        val metrics = context.resources.displayMetrics
        val top = (OFFSET_TOP * metrics.density).toInt()
        val height = metrics.heightPixels - top
        val width = height * Dimensions.SCREEN_WIDTH / Dimensions.SCREEN_HEIGHT
        val left = (metrics.widthPixels - width) / 2
        dstRect = Rect(left, top, left + width, top + height)
    }

    fun start() {
        PlatformHolder.setPlatform(this)
        thread.start {
            setOnTouchListener(Mouse())
            repaint()
            controller.start()
        }
    }

    // If `sync` is true, it waits for `thread` to terminate.
    fun quit(sync: Boolean) {
        setOnTouchListener(null)
        thread.quit()
        if (sync) {
            thread.join()
        }
        Timer.release()
        PlatformHolder.setPlatform(null)
    }

    override fun exit() {
        quit(false)
        dosScreen = DosScreen(dstRect.toRectF(), context, this::postInvalidate)
    }

    override fun getAppDirPath(): String {
        return context.filesDir.path
    }

    override fun getResourceAsStream(fileName: String): InputStream {
        return context.assets.open(fileName)
    }

    override fun getBufferedImage(fileName: String): BufferedImage {
        context.assets.open(fileName).use { stream ->
            return BufferedImage(BitmapFactory.decodeStream(stream))
        }
    }

    override fun getSound(fileName: String, listener: Runnable): Sound {
        context.assets.openFd(fileName).use { fd ->
            return SimpleSound(fd, listener, this::repaint)
        }
    }

    override fun startSecondaryLoop() {
        thread.startNestedLoop()
    }

    override fun stopSecondaryLoop() {
        thread.stopNestedLoop()
    }

    override fun invokeLater(action: Runnable, delay: Int) {
        thread.postDelayed({
            repaint()
            action.run()
            // NOTE: This point may be unreachable.
        }, delay)
    }

    override fun isVirtualKeyboardRequired(): Boolean {
        return true
    }

    override fun onDraw(canvas: Canvas) {
        dosScreen?.let {
            it.paint(canvas)
            return
        }
        canvas.drawBitmap(frontBuffer.bitmap, null, dstRect, null)
    }

    private fun repaint() {
        thread.post(schedulePaint)
    }

    private inner class Mouse : OnTouchListener {
        private var lastUpEventTime = 0L
        private var clickCount = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val x = (event.x.toInt() - dstRect.left) * Dimensions.SCREEN_WIDTH / dstRect.width()
            val y = (event.y.toInt() - dstRect.top) * Dimensions.SCREEN_HEIGHT / dstRect.height()
            val e = MouseEvent(x, y)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (SystemClock.uptimeMillis() - lastUpEventTime < ViewConfiguration.getDoubleTapTimeout()) {
                        ++clickCount
                    } else {
                        clickCount = 1
                    }
                    e.clickCount = clickCount
                    thread.post { controller.mousePressed(e) }
                }

                MotionEvent.ACTION_UP -> {
                    lastUpEventTime = SystemClock.uptimeMillis()
                    thread.post { controller.mouseReleased(e) }
                }

                MotionEvent.ACTION_MOVE -> {
                    thread.post { controller.mouseDragged(e) }
                }

                else -> {
                    Logger.warn("Unprocessed touch event: $event")
                    return false
                }
            }
            repaint()
            return true
        }
    }
}
