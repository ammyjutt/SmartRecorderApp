package com.example.smartrecorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class RecordingService : Service() {

    private var mediaRecorder: MediaRecorder? = null
    private val fileName: String by lazy {
        "${getExternalFilesDir(null)?.absolutePath}/audiorecordtest.3gp"
    }
    private val channelId = "RecordingServiceChannel"

    override fun onCreate() {
        Log.d("RecordingService", "Service created")
        super.onCreate()
        createNotificationChannel()
        startForegroundService()
        startRecording()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("RecordingService", "Service started")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
                start()
                Log.d("RecordingService", "Recording started at $fileName")
            } catch (e: IOException) {
                Log.e("RecordingService", "Failed to start recording", e)
                stopSelf()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        Log.d("RecordingService", "Recording stopped")
    }

    override fun onDestroy() {
        stopRecording()
        super.onDestroy()
    }

    private fun startForegroundService() {
        Log.d("RecordingService", "Entered Start Foreground")

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Recording in Progress")
            .setContentText("Tap to return to the app")
            .setSmallIcon(android.R.drawable.ic_menu_revert)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()


        Log.d("RecordingService", "Special Point") // it reaches this point
        startForeground(1, notification)
        Log.d("RecordingService", "Notification started") // but , doesn't reach this point
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recording Service Channel"
            val descriptionText = "Channel for recording service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("RecordingService", "Notification channel created")
        }
    }
}
