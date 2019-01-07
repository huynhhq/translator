package com.example.dev00.translator.models

data class AppData(var leftFlag: Flag?
                   , var rightFlag: Flag?
                   , var appSettingData: AppSettingData
                   , var arrTranslateData: ArrayList<SpTextData>)