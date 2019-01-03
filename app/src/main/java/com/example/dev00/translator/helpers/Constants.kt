package com.example.dev00.translator.helpers

class Constants {
    companion object {
        val JSON_RESOURCE_FILE_GOOGLE = "google_flags_source.json"
        val JSON_RESOURCE_FILE_YANDEX = "yandex_flags_source.json"
        val JSON_TYPE_GOOGLE_FLAGS = "google_flags"
        val JSON_TYPE_YANDEX_FLAGS = "yandex_flags"
        val TITLE_TOOLBAR = "Voice Translator"
        val TITLE_SETTING = "Setting"
        val TITLE_ABOUT = "About us"
        val LEFT_LANGUAGE: Int = 0
        val RIGHT_LANGUAGE: Int = 1
        val SPEECH_RECOGNITION_CODE = 1
        val JSON_FIELD_NAME = "name"
        val JSON_FIELD_IMAGE = "image"
        val JSON_FIELD_LANGUAGE_CODE = "language_code"
        var LEFT_MODE = 0
        var RIGHT_MODE = 1
        val YANDEX_API = 0
        val GOOGLE_API = 1
        val LANGUAGE_VN = "VN"
        val LANGUAGE_ENGLISH = "ENGLISH"

        //inital first languages
        val FROM_LANGUAGE_CODE_GG = "vi-VN"
        val TO_LANGUAGE_CODE_GG = "ja-JP"
        val FROM_LANGUAGE_CODE_YD = "vi"
        val TO_LANGUAGE_CODE_YD = "ja"
        val FROM_IMG_PATH = "flags/VNM.png"
        val TO_IMG_PATH = "flags/JPN.png"
        val FROM_NAME = "Vietnamese"
        val TO_NAME = "Japanese"
        //end inital first languages

        //Message
        val SPEECH_RECOGNITION_NOT_SUPPORT="Sorry! Speech recognition is not supported in this device."
        val MUST_IMPLEMENT_ONFRAGMENTINTERRACTIONLISTENER = " must implement OnFragmentInteractionListener"
        val CHECK_YOUR_INTERNET = "Please check your internet and try again!!!"
        //end Message

        //Key API
        val YANDEX_KEY = "trnsl.1.1.20181206T043553Z.e749c47e3c3f08f3.3c2eceb80225b69ce6c4168bfcc526218842c890"
        val GOOGLE_KEY = ""
        val BASE_ADDRESS_YANDEX = "https://translate.yandex.net"
        val BASE_ADDRESS_GOOGLE = "https://translation.googleapis.com"
        //End kye API
    }
}