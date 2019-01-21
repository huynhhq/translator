package com.example.dev00.translator.models

import com.example.dev00.translator.interfaces.ConversionCallback
import com.example.dev00.translator.interfaces.IConvertor
import com.example.dev00.translator.services.SpeechToTextService
import com.example.dev00.translator.services.TextToSpeechService

class TranslatorFactory private constructor() {

    enum class TRANSLATOR_TYPE {
        TEXT_TO_SPEECH, SPEECH_TO_TEXT
    }

    companion object {
        private var m_instance: TranslatorFactory = TranslatorFactory()
        @Synchronized
        fun getInstance(): TranslatorFactory {
            return m_instance
        }
    }

    fun getTranslator(translator_type: TRANSLATOR_TYPE, consersionCallback: ConversionCallback): IConvertor? {
        when(translator_type){
            TRANSLATOR_TYPE.TEXT_TO_SPEECH -> {
                return TextToSpeechService(consersionCallback)
            }

            TRANSLATOR_TYPE.SPEECH_TO_TEXT -> {
                return SpeechToTextService(consersionCallback)
            }
        }
        return null
    }
}