package com.example.dev00.translator.interfaces

import android.app.Activity



interface IConvertor {
    fun initialize(message: String, appContext: Activity, localeStr: String): IConvertor
}