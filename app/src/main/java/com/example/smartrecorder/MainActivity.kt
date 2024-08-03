package com.example.smartrecorder

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.content.ContextWrapper
import android.content.Context

class MainActivity : Activity() {

    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private lateinit var recordButton: Button
    private val requestCode = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.recordButton)

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), requestCode)
        }
    }

    private fun startRecording() {
        val fileName = "${getExternalFilesDir(null)?.absolutePath}/audiorecordtest.3gp"
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)
            try {
                prepare()
                start()
                Log.d("SmartRecorder", "Recording started at $fileName")
                recordButton.text = "Stop Recording"
                isRecording = true
            } catch (e: Exception) {
                Log.e("SmartRecorder", "Failed to start recording", e)
                release()
            }
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            Log.d("SmartRecorder", "Recording stopped")
        } catch (e: Exception) {
            Log.e("SmartRecorder", "Failed to stop recording", e)
        }
        recordButton.text = "Start Recording"
        isRecording = false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
                Log.d("SmartRecorder", "Record audio permission granted")
            } else {
                // Permission denied
                Log.d("SmartRecorder", "Record audio permission denied")
            }
        }
    }
}