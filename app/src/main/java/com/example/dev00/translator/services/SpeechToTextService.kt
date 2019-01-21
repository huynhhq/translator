package com.example.dev00.translator.services

import android.app.Activity
import com.example.dev00.translator.interfaces.IConvertor
import android.speech.RecognizerIntent
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.dev00.translator.interfaces.ConversionCallback
import com.example.dev00.translator.utils.Utils.Companion.getErrorMessage
import java.util.*

class SpeechToTextService : IConvertor {

    private var conversionCallback: ConversionCallback

    constructor(conversionCallback: ConversionCallback) {
        this.conversionCallback = conversionCallback
    }

    override fun initialize(message: String, appContext: Activity, languageCode: String): IConvertor {
        //Prepare Intent
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                message)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                appContext.packageName)

        //Add Listenters
        val listener = CustomRecognitionListener()
        val sr = SpeechRecognizer.createSpeechRecognizer(appContext)
        sr.setRecognitionListener(listener)
        sr.startListening(intent)

        return this
    }

    inner class CustomRecognitionListener : RecognitionListener {

        private val TAG = CustomRecognitionListener::class.java.name

        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "onReadyForSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d(TAG, "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            Log.d(TAG, "onBufferReceived")
        }

        override fun onPartialResults(partialResults: Bundle?) {
            Log.d(TAG, "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            Log.d(TAG, "onEvent " + eventType)
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech")
        }

        override fun onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech")
        }

        override fun onError(error: Int) {
            Log.e(TAG, "error " + error)
            conversionCallback.onErrorOccured(getErrorMessage(error))
        }

        override fun onResults(results: Bundle?) {
            Log.d(TAG, "onResults " + results)
            var data = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            conversionCallback.onSuccess(data.get(0))
        }
    }

}