package com.example.dev00.translator.services

import android.app.Activity
import com.example.dev00.translator.interfaces.ConversionCallback
import com.example.dev00.translator.interfaces.IConvertor
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.dev00.translator.utils.Utils
import java.util.*
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.annotation.TargetApi
import android.os.Build


class TextToSpeechService: IConvertor {

    private var conversionCallback: ConversionCallback

    private var textToSpeech: TextToSpeech? = null

    constructor(conversionCallback: ConversionCallback) {
        this.conversionCallback = conversionCallback
    }

    override fun initialize(message: String, appContext: Activity, localeStr: String): IConvertor {
        textToSpeech = TextToSpeech(appContext, TextToSpeech.OnInitListener {
            if(it == TextToSpeech.SUCCESS){
                val locale = Locale(localeStr)
                textToSpeech!!.setLanguage(locale)
                textToSpeech!!.setPitch(1.3f);
                textToSpeech!!.setSpeechRate(1f);

                speakOut(message)
            }
        })

        return this
    }

    private fun speakOut(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech!!.speak(message,TextToSpeech.QUEUE_FLUSH,null,null);
        } else {
            textToSpeech!!.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}