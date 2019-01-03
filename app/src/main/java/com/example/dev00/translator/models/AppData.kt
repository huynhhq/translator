package com.example.dev00.translator.models

data class AppData(var leftFlag: Flag?
                   , var rightFlag: Flag?
                   , var api: Int?
                   , var languageApp: String?
                   , var arrTranslateData: ArrayList<SpTextData>)