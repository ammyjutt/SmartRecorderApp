package com.example.smartrecorder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity() {

    private var isRecording = false
    private lateinit var recordButton: Button
    private val requestCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.recordButton)

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecordingService()
                recordButton.text = "Start Recording"
            } else {
                checkAndRequestPermissions()
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), requestCode)
        } else {
            startRecordingService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordingService()
            } else {
                Log.e("MainActivity", "Record audio permission denied")
            }
        }
    }

    private fun startRecordingService() {
        val serviceIntent = Intent(this, RecordingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        isRecording = true
        recordButton.text = "Stop Recording"
        Log.d("MainActivity", "Started Recording Service")
    }

    private fun stopRecordingService() {
        val serviceIntent = Intent(this, RecordingService::class.java)
        stopService(serviceIntent)
        isRecording = false
    }
}
