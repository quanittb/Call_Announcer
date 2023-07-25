package com.mobiai.app.ultils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.mobiai.app.App
import java.text.SimpleDateFormat
import java.util.Date

class AudioControllerRingtone (val context: Context) {
    private val TAG: String = AudioControllerRingtone::javaClass.name
    private val STATE_NEW = 0
    private val STATE_IDLE = 1
    private val STATE_PLAYING = 2
    private val STATE_PAUSED = 3
    private var state = STATE_NEW
    private var mediaPlayer: MediaPlayer? = null

    fun play(idRingtone: Any, onCompleted: () -> Unit) {

        when (state) {
            STATE_NEW -> {
                setupMediaPlayer(idRingtone, onCompleted)
            }

            STATE_IDLE -> {
                mediaPlayer!!.reset()
                setupMediaPlayer(idRingtone, onCompleted)
            }
        }
    }

    private fun setupMediaPlayer(idRingtone: Any, onCompleted: () -> Unit) {
        stopSystemMusic()
        when (idRingtone) {
            is Int -> {
                mediaPlayer = MediaPlayer.create(App.getInstance(), idRingtone)
                mediaPlayer!!.setOnCompletionListener {
                    onCompleted()
                    state = STATE_IDLE
                }
                mediaPlayer!!.setOnPreparedListener {
                    mediaPlayer!!.start()
                    state = STATE_PLAYING
                }
            }

            is Uri -> {
                mediaPlayer = MediaPlayer()
                mediaPlayer!!.setDataSource(context, idRingtone)
                mediaPlayer!!.setOnCompletionListener {
                    onCompleted()
                    state = STATE_IDLE
                }
                mediaPlayer!!.setOnPreparedListener { mp ->
                    mp.start()
                    state = STATE_PLAYING
                }
                mediaPlayer!!.prepareAsync()
            }
        }

    }


    fun play(idRingtone: Uri, onCompleted: () -> Unit) {

        when (state) {
            STATE_NEW -> {
                setupMediaPlayer(idRingtone, onCompleted)
            }

            STATE_IDLE -> {
                mediaPlayer!!.reset()
                setupMediaPlayer(idRingtone, onCompleted)
            }

            STATE_PLAYING -> {
                pause()
            }

            STATE_PAUSED -> {
                continuePlaying()
            }
        }
    }

    fun pause() {
        if (mediaPlayer != null && state == STATE_PLAYING) {
            mediaPlayer!!.pause()
            state = STATE_PAUSED
        }
    }

    fun stop() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            state = STATE_IDLE
        }
    }

    fun continuePlaying() {
        if (mediaPlayer != null && state == STATE_PAUSED) {
            mediaPlayer!!.start()
            state = STATE_PLAYING
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer != null && state == STATE_PLAYING
    }

    fun isPause(): Boolean {
        return mediaPlayer != null && state == STATE_PAUSED
    }

    fun isComplete(): Boolean {
        return mediaPlayer != null && state == STATE_IDLE
    }

    fun isNotInitialized(): Boolean {
        return mediaPlayer == null && state == STATE_NEW
    }

    fun getCurrentTimeDuration(): Int {
        if (mediaPlayer != null) {
            return mediaPlayer!!.currentPosition
        }
        return 0
    }

    fun getTotalTimeDuration(): Int {
        if (mediaPlayer != null) {
            return mediaPlayer!!.duration
        }
        return 0
    }

    fun getCurrentTimeText(): String {

        if (mediaPlayer != null) {
            val df = SimpleDateFormat("mm:ss")
            return df.format(Date(mediaPlayer!!.currentPosition.toLong()))
        }
        return "--"
    }

    fun getTotalTimeText(): String? {
        try {
            val df = SimpleDateFormat("mm:ss")
            return df.format(Date(mediaPlayer!!.getDuration().toLong()))
        } catch (e: Exception) {
        }
        return "--"
    }

    fun reLease() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
            state = STATE_NEW
        }
    }

    private fun stopSystemMusic() {
        val  audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN);
        if (audioManager.isMusicActive) {
            val i = Intent("com.android.music.musicservicecommand")
            i.putExtra("command", "pause")
            context.applicationContext.sendBroadcast(i)
        }
    }
}