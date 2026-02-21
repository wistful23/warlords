package com.def.warlords.android

import android.os.Handler
import android.os.Looper

internal class LooperThread : Thread() {
    private var listener: Runnable? = null
    private var handler: Handler? = null

    // Must be called once.
    fun start(listener: Runnable) {
        this.listener = listener
        start()
    }

    // Can be called on any thread.
    fun quit() {
        handler!!.looper.quit()
    }

    // Must be called on this thread.
    fun startNestedLoop() {
        try {
            Looper.loop()
        } catch (_: StopLoopException) {
        }
    }

    // Must be called on this thread.
    fun stopNestedLoop() {
        // NOTE: We can't call `Looper.quit()` here since it quits the parent loop as well.
        throw StopLoopException()
    }

    // Can be called on any thread.
    fun post(action: Runnable) {
        handler!!.post(action)
    }

    // Can be called on any thread.
    fun postDelayed(action: Runnable, delay: Int) {
        if (delay > 0) {
            handler!!.postDelayed(action, delay.toLong())
        } else {
            handler!!.post(action)
        }
    }

    override fun run() {
        Looper.prepare()
        handler = Handler(Looper.myLooper()!!)
        handler!!.post(listener!!)
        Looper.loop()
    }

    private class StopLoopException : RuntimeException()
}
