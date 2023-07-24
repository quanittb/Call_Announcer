package com.mobiai.app.ultils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer

class AudioController() {
    private var audioManager: AudioManager? = null
    private var context: Context? = null
    private var mediaPlayer: MediaPlayer? = null

    constructor(context: Context) : this() {
        this.context = context
        audioManager =
            context.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun getMaxVolume(): Int {
        return audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0
    }

    fun setVolume(volIndex: Int) {
        audioManager?.setStreamVolume(
            AudioManager.STREAM_MUSIC, volIndex, 0
        )
    }

    fun getCurrentVolume(): Int {
        return audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
    }

    fun adjustVolumeLower() {
        audioManager?.adjustStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI
        )
    }

    fun adjustVolumeHigher() {
        audioManager?.adjustStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI
        )
    }
}
