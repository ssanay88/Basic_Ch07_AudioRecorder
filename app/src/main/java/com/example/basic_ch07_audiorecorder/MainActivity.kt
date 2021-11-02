package com.example.basic_ch07_audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.basic_ch07_audiorecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding : ActivityMainBinding

    private var state = State.BEFORE_RECODING   // 초기 상태는 녹음 전
    set(value) {
        field = value
        mainBinding.resetBtn.isEnabled = (value == State.AFTER_RECODING) || (value == State.ON_PLAYING)
        mainBinding.recordBtn.updateIconWithState(value)
    }

    private val requiredPermissions = arrayOf(Manifest.permission.RECORD_AUDIO , Manifest.permission.READ_EXTERNAL_STORAGE)

    private var recorder:MediaRecorder? = null
    private var player:MediaPlayer? = null

    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted =
            (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) && (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED)

        if (!audioRecordPermissionGranted) {
            finish()    // 앱 종료
        }

    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions , REQUEST_RECORD_AUDIO_PERMISSION)
    }

    private fun initViews() {
        mainBinding.recordBtn.updateIconWithState(state)
    }

    private fun bindViews() {

        mainBinding.SoundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        mainBinding.recordBtn.setOnClickListener {
            when(state) {
                State.BEFORE_RECODING -> {
                    startRecording()
                }

                State.ON_RECODING -> {
                    stopRecording()
                }

                State.AFTER_RECODING -> {
                    startPlaying()
                }

                State.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }

        mainBinding.resetBtn.setOnClickListener {
            stopPlaying()
            mainBinding.SoundVisualizerView.clearVisualization()
            mainBinding.recordTimeTextView.clearCountUp()
            state = State.BEFORE_RECODING
        }


    }

    private fun initVariables() {
        state = State.BEFORE_RECODING
    }


    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }

        // 전달된 파일을 모두 재생했을 경우 실행
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECODING
        }

        player?.start()
        mainBinding.SoundVisualizerView.startVisualizing(true)
        mainBinding.recordTimeTextView.startCountUp()

        state = State.ON_PLAYING
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        mainBinding.SoundVisualizerView.stopVisualizing()
        mainBinding.recordTimeTextView.stopCountUp()

        state = State.AFTER_RECODING
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()
        mainBinding.SoundVisualizerView.startVisualizing(false)
        mainBinding.recordTimeTextView.startCountUp()

        state = State.ON_RECODING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }

        recorder = null
        mainBinding.SoundVisualizerView.stopVisualizing()
        mainBinding.recordTimeTextView.stopCountUp()

        state = State.AFTER_RECODING
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }




}

/*
companion object?
costomSetter?
invoke?


 */