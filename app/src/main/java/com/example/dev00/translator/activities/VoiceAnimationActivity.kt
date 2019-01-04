package com.example.dev00.translator.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.example.dev00.translator.R

class VoiceAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_voice_animation)
    }
}
