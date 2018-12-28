package com.example.dev00.translator.services

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.speech.tts.TextToSpeech
import com.example.dev00.translator.utils.Utils
import java.util.*

class TTS(private val context: Context
          , private val message: String
          , private val localeStr: String) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this)

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {

            val locale = Locale(localeStr)

            val result: Int
            result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Utils.createToast(context, "This Language is not supported")
            } else {
                speakOut(message)
            }

        } else {
            Utils.createToast(context, "Initilization Failed!")
        }
    }

    private fun speakOut(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
