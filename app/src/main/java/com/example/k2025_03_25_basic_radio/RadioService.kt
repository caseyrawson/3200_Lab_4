package com.example.k2025_03_25_basic_radio

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder

class RadioService : Service() {

    private val binder = RadioBinder()
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var radioThread: HandlerThread
    private lateinit var radioHandler: Handler

    override fun onCreate() {
        super.onCreate()
        // Start a background thread
        radioThread = HandlerThread("RadioThread").apply { start() }
        radioHandler = Handler(radioThread.looper)
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer
        radioHandler.post {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
        radioThread.quitSafely()
    }

    inner class RadioBinder : Binder() {
        fun getService(): RadioService = this@RadioService
    }

    fun playRadio(url: String) {
        radioHandler.post {
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                mediaPlayer.reset()
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(url)
                mediaPlayer.setOnPreparedListener { mp ->
                    mp.start()
                }
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRadio() {
        radioHandler.post {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
        }
    }
}
