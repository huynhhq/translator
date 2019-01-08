package com.example.dev00.translator.helpers

class Constants {
    companion object {
        val JSON_RESOURCE_FILE_GOOGLE = "google_flags_source.json"
        val JSON_RESOURCE_FILE_YANDEX = "yandex_flags_source.json"
        val JSON_TYPE_GOOGLE_FLAGS = "google_flags"
        val JSON_TYPE_YANDEX_FLAGS = "yandex_flags"
        val TITLE_TOOLBAR = "Voice Translator"
        val LEFT_LANGUAGE: Int = 0
        val RIGHT_LANGUAGE: Int = 1
        val SPEECH_RECOGNITION_CODE = 1
        val JSON_FIELD_NAME = "name"
        val JSON_FIELD_IMAGE = "image"
        val JSON_FIELD_LANGUAGE_CODE = "language_code"
        val JSON_FIELD_ASR_CODE = "asr_code"
        var LEFT_MODE = 0
        var RIGHT_MODE = 1
        val YANDEX_API = 0
        val GOOGLE_API = 1
        val LANGUAGE_VN = "VN"
        val LANGUAGE_EN = "ENGLISH"
        val LANGUAGE_JP = "JAPANESE"

        //inital first languages
        val FROM_LANGUAGE_CODE_YD = "vi"
        val TO_LANGUAGE_CODE_YD = "ja"
        val FROM_IMG_PATH = "flags/VNM.png"
        val FROM_ASR_CODE = "vie-VNM"
        val TO_IMG_PATH = "flags/JPN.png"
        val FROM_NAME = "Vietnamese"
        val TO_NAME = "Japanese"
        val TO_ASR_CODE = "jpn-JPN"
        //end inital first languages

        val MUST_IMPLEMENT_ONFRAGMENTINTERRACTIONLISTENER = " must implement OnFragmentInteractionListener"
        val CHECK_YOUR_INTERNET = "Please check your internet and try again!!!"

    }
}