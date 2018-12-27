package com.example.dev00.translator.models

data class GoogleResponse(var data: Data)

data class Data(val translations: List<Translation>)

data class Translation(val translatedText: String, val detectedSourceLanguage: String)