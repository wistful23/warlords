package com.def.warlords.android

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import com.def.warlords.sound.Player

// The basic audio player that plays an audio track once and cannot be reused.
class AudioPlayer(fd: AssetFileDescriptor, listener: Runnable, repaint: Runnable) : Player {
    private var player = MediaPlayer()

    init {
        player.setDataSource(fd)
        player.prepare()
        player.setOnCompletionListener {
            player.release()
            repaint.run()
            listener.run()
            // NOTE: This point may be unreachable.
        }
    }

    override fun start() {
        player.start()
    }

    override fun stop() {
        player.release()
    }
}
