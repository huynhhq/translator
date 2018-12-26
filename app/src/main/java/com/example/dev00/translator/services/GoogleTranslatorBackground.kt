package com.example.dev00.translator.services

import android.content.Context
import android.os.AsyncTask
import com.example.dev00.translator.helpers.Constants
import java.io.IOException

class GoogleTranslatorBackground (val context: Context) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String?): String? {
        val textToBeTranslated: String? = params[0]
        val languagePair: String? = params[1]
        try {

            val serverURL = ""
            val googleAPIKey = Constants.GOOGLE_KEY
        }catch (e: Throwable) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}