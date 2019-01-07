package com.example.dev00.translator.models

import android.content.Context
import android.content.SharedPreferences
import com.example.dev00.translator.helpers.Constants
import com.google.gson.Gson

class Prefs(context: Context) {
    val PREFS_FILENAME = "com.voicetranslator.settingapp.prefs"
    val APP_SETTING_DATA = "AppSettingData"


    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)

    var appSettingData: String
        get() = prefs.getString(APP_SETTING_DATA, null)

        set(value) {
            var prefsEditor = prefs.edit()
            prefsEditor.putString(APP_SETTING_DATA, value).apply()
        }
}