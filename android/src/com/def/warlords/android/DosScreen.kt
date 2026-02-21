package com.def.warlords.android

import android.content.Context
import android.graphics.*
import com.def.warlords.util.Timer

private const val TEXT_SIZE = 64f

private const val DELAY_CURSOR_ANIMATION = 200

class DosScreen(private val dstRect: RectF, context: Context, repaint: Runnable) {
    private val fontPaint: Paint = Paint()

    private var showCursor = true
    private val timer = Timer { showCursor = !showCursor; repaint.run() }

    init {
        fontPaint.typeface = Typeface.createFromAsset(context.assets, "dos.ttf")
        fontPaint.textSize = TEXT_SIZE
        fontPaint.color = Color.LTGRAY
        timer.start(DELAY_CURSOR_ANIMATION)
    }

    fun paint(canvas: Canvas) {
        canvas.drawText("Warlords Version 2.10  (c) Strategic Studies Group", dstRect.left, dstRect.top, fontPaint)
        canvas.drawText(
            "C:/GAMES/WARLORDS>" + if (showCursor) "_" else "",
            dstRect.left, dstRect.top + TEXT_SIZE * 2, fontPaint
        )
    }
}
