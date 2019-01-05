package com.example.dev00.translator.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.VoiceView
import android.os.Handler
import android.widget.Toast
import android.util.Log
import android.view.View
import com.example.dev00.translator.helpers.Credentials
import com.example.dev00.translator.helpers.State
import com.example.dev00.translator.utils.ScreenUtils
import com.example.dev00.translator.utils.Utils
import com.nuance.speechkit.*
import kotlinx.android.synthetic.main.activity_voice_animation.*

class VoiceAnimationActivity : AppCompatActivity(), VoiceView.OnRecordListener {

    private val TAG = VoiceAnimationActivity::class.java.name
    private var mIsRecording = true
    private lateinit var context: Context

    private lateinit var speechSession: Session
    private var state = State.IDLE
    private var recoTransaction: Transaction? = null
    private var startEarcon: Audio? = null
    private var stopEarcon: Audio? = null
    private var errorEarcon: Audio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_animation)

        initalVar()
        initalListener()

        recognize()
        startAudioLevelPoll()
    }

    private fun initalVar() {
        context = this
        speechSession = Session.Factory.session(this, Credentials.SERVER_URI, Credentials.APP_KEY)

        loadEarcons()
        setState(State.IDLE)
    }

    private fun initalListener() {
        voice_view.setOnRecordListener(this)
    }

    override fun onRecordStart() {
        Log.d(TAG, "onRecordStart")
        try {
            startAudioLevelPoll()
        } catch (e: Exception) {
            Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onRecordFinish() {
        Log.d(TAG, "onRecordFinish");
        mIsRecording = false
        stopAudioLevelPoll()
    }

    override fun onDestroy() {
        if (mIsRecording) {
            onRecordFinish()
        }
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        when (state) {
            State.IDLE -> {

            }
            State.LISTENING -> {
                stopRecording()
            }
            State.IDLE -> {
                cancel()
            }
        }
    }

    /**
     * Stop recording the user
     */
    private fun stopRecording() {
        recoTransaction!!.stopRecording()
    }

    /**
     * Cancel the Reco transaction.
     * This will only cancel if we have not received a response from the server yet.
     */
    private fun cancel() {
        recoTransaction!!.cancel()
        setState(State.IDLE)
    }

    /**
     * Set the state and update the button text.
     */
    private fun setState(newState: State) {
        state = newState
        when (newState) {
            State.IDLE -> {
                //dothing
            }
            State.LISTENING -> {
                Utils.createToast(context, "Listening")
            }
            State.PROCESSING -> {
                voice_view.visibility = View.INVISIBLE
                tv_status.text = "Processing..."
            }
            State.DONE -> {
                voice_view.visibility = View.VISIBLE
                tv_status.text = ""
                voice_view.change2btnPressed()
            }
            State.ERROR -> {
                tv_title.text = "Đã có lỗi xảy ra. Vui lòng thử lại."
                voice_view.change2btnOff()
            }
        }
    }

    /**
     * Start listening to the user and streaming their voice to the server.
     */
    private fun recognize() {
        //Setup our Reco transaction options.
        val options = Transaction.Options()
        options.recognitionType = RecognitionType.DICTATION
        options.detection = DetectionType.Short
        options.language = Language("en_US")
        options.setEarcons(startEarcon, stopEarcon, errorEarcon, null)

        //Start listening
        recoTransaction = speechSession.recognize(options, recoListener)
    }

    private val recoListener = object : Transaction.Listener() {
        override fun onStartedRecording(transaction: Transaction?) {
            Utils.createToast(context, "\nonStartedRecording")

            //We have started recording the users voice.
            //We should update our state and start polling their volume.
            setState(State.LISTENING)
        }

        override fun onFinishedRecording(transaction: Transaction?) {
            Utils.createToast(context, "\nonFinishedRecording")
            //We have finished recording the users voice.
            //We should update our state and stop polling their volume.
            setState(State.PROCESSING)
        }

        override fun onRecognition(transaction: Transaction?, recognition: Recognition) {
            Utils.createToast(context, "\nonRecognition: " + recognition.text)

            //We have received a transcription of the users voice from the server.
        }

        override fun onSuccess(transaction: Transaction?, s: String?) {
            Utils.createToast(context, "\nonSuccess")

            //Notification of a successful transaction.
            setState(State.DONE)
        }

        override fun onError(transaction: Transaction?, s: String?, e: TransactionException) {
            Utils.createToast(context, "\nonError: " + e.message + ". " + s)
            //Something went wrong. Check Configuration.java to ensure that your settings are correct.
            //The user could also be offline, so be sure to handle this case appropriately.
            //We will simply reset to the idle state.
            setState(State.ERROR)
        }
    }

    private fun loadEarcons() {
        //Load all the earcons from disk
        startEarcon = Audio(context, R.raw.sk_start, Credentials.PCM_FORMAT)
        stopEarcon = Audio(context, R.raw.sk_stop, Credentials.PCM_FORMAT)
        errorEarcon = Audio(context, R.raw.sk_error, Credentials.PCM_FORMAT)
    }

    /* Audio Level Polling */

    private val handler = Handler()

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private val audioPoller = object : Runnable {
        override fun run() {
            val level = recoTransaction!!.getAudioLevel()
            val radius = Math.log10(Math.max(1.toDouble(), (level * 10).toDouble())).toFloat() * ScreenUtils.dp2px(this@VoiceAnimationActivity, 15)
            voice_view.animateRadius(radius)
            handler.postDelayed(this, 50)
        }
    }

    /**
     * Start polling the users audio level.
     */
    private fun startAudioLevelPoll() {
        audioPoller.run()
    }

    /**
     * Stop polling the users audio level.
     */
    private fun stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller)
    }
}
