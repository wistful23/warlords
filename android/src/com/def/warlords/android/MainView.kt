package com.def.warlords.android

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.def.warlords.control.MainController
import com.def.warlords.control.Platform
import com.def.warlords.control.common.Dimensions
import com.def.warlords.graphics.BitmapFactory
import com.def.warlords.sound.Player
import com.def.warlords.sound.SoundFactory
import com.def.warlords.util.Logger
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.InputStream

class MainView(private val context: Context) : View(context), Platform {
    private val controller: MainController
    private val thread = LooperThread()

    private val dstRect = Rect()
    private var backBuffer = Graphics(Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT)
    private var frontBuffer = Graphics(Dimensions.SCREEN_WIDTH, Dimensions.SCREEN_HEIGHT)

    init {
        BitmapFactory.createInstance(this)
        SoundFactory.createInstance(this)
        this.controller = MainController(this)
    }

    fun start() {
        thread.start {
            setOnTouchListener(Mouse())
            repaint()
            controller.start()
        }
    }

    fun repaint() {
        thread.post {
            controller.paint(backBuffer)
            // Swap the buffers.
            frontBuffer = backBuffer.also { backBuffer = frontBuffer }
            // Enqueue `View.onDraw()`.
            postInvalidate()
        }
    }

    override fun getResourceAsStream(fileName: String): InputStream {
        return context.assets.open(fileName)
    }

    override fun getBufferedImage(fileName: String): BufferedImage {
        context.assets.open(fileName).use { stream ->
            return BufferedImage(android.graphics.BitmapFactory.decodeStream(stream))
        }
    }

    override fun getAudioPlayer(fileName: String, listener: Runnable): Player {
        context.assets.openFd(fileName).use { fd ->
            return AudioPlayer(fd, listener, this::repaint)
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

    override fun onDraw(canvas: Canvas) {
        val metrics = context.resources.displayMetrics
        dstRect.right = metrics.widthPixels
        dstRect.bottom = metrics.heightPixels
        canvas.drawBitmap(frontBuffer.bitmap, null, dstRect, null)
    }

    private inner class Mouse : OnTouchListener {
        private var lastUpEventTime: Long = 0
        private var clickCount = 0

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val metrics = context.resources.displayMetrics
            val x = event.x.toInt() * Dimensions.SCREEN_WIDTH / metrics.widthPixels
            val y = event.y.toInt() * Dimensions.SCREEN_HEIGHT / metrics.heightPixels
            val e = MouseEvent(x, y, clickCount)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (System.currentTimeMillis() - lastUpEventTime < ViewConfiguration.getDoubleTapTimeout()) {
                        ++clickCount
                    } else {
                        clickCount = 1
                    }
                    thread.post { controller.mousePressed(e) }
                }

                MotionEvent.ACTION_UP -> {
                    lastUpEventTime = System.currentTimeMillis()
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
