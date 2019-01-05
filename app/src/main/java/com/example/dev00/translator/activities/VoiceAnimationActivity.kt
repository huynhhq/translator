package com.example.dev00.translator.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.VoiceView
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.util.Log
import com.example.dev00.translator.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_voice_animation.*

class VoiceAnimationActivity : AppCompatActivity(), VoiceView.OnRecordListener {

    private val TAG = MainActivity::class.java.name
    private var mHandler: Handler? = null
    private var mMediaRecorder: MediaRecorder? = null
    private var mIsRecording = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_voice_animation)
        voice_view.setOnRecordListener(this)
        mHandler = Handler(Looper.getMainLooper())
        onRecordStart()
    }

    override fun onRecordStart() {
        Log.d(TAG, "onRecordStart")
        try {
            mMediaRecorder = MediaRecorder()
            mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder!!.setOutputFile("/dev/null");
            mMediaRecorder!!.prepare()
            mMediaRecorder!!.start()
            mIsRecording = true;
            mHandler!!.post(object : Runnable {
                override fun run() {
                    val radius = Math.log10(Math.max(1.toDouble(), (mMediaRecorder!!.getMaxAmplitude() - 500).toDouble())).toFloat() * ScreenUtils.dp2px(this@VoiceAnimationActivity, 15)
                    voice_view.animateRadius(radius)
                    if (mIsRecording) {
                        mHandler!!.postDelayed(this, 50)
                    }
                }
            })
        } catch (e: Exception) {
            Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onRecordFinish() {
        Log.d(TAG, "onRecordFinish");
        mIsRecording = false
        mMediaRecorder!!.stop()
        mHandler!!.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        if(mIsRecording){
           onRecordFinish()
        }
        mMediaRecorder!!.release()
        super.onDestroy()
    }
}
