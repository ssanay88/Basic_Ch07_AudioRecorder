package com.example.basic_ch07_audiorecorder

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.appcompat.widget.AppCompatImageButton


class RecordButton(context: Context , attrs: AttributeSet) : AppCompatImageButton(context , attrs) {

    fun updateIconWithState(state: State) {
        when (state) {

            // 녹음 전
           State.BEFORE_RECODING -> {
                setImageResource(R.drawable.ic_record)
            }

            // 녹음 중일 때
            State.ON_RECODING -> {
                setImageResource(R.drawable.ic_stop)
            }

            // 녹음이 끝난 뒤
            State.AFTER_RECODING -> {
                setImageResource(R.drawable.ic_play)
            }

            // 녹음한 내용을 재생중일 때
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_stop)
            }
        }
    }



}

// AppCompatImageButton : 이전 안드로이드 버전에서도 호환되게 하기 위한 라이브러리