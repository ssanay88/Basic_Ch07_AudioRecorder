package com.example.basic_ch07_audiorecorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.basic_ch07_audiorecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding : ActivityMainBinding

    private var state = State.BEFORE_RECODING   // 초기 상태는 녹음 전

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        initViews()

    }

    private fun initViews() {
        mainBinding.recordBtn.updateIconWithState(state)
    }









}