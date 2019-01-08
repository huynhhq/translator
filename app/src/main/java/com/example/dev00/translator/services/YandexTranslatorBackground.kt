package com.example.dev00.translator.services

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.example.dev00.translator.helpers.Credentials
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class YandexTranslatorBackground(val context: Context) : AsyncTask<String, Void, String>() {

    //old code to call yandex API
    override fun doInBackground(vararg params: String?): String? {
        val textToBeTranslated: String? = params[0]
        val languagePair: String? = params[1]

        try {
            var jsonString: String? = null
            val encodeText = URLEncoder.encode(textToBeTranslated, StandardCharsets.UTF_8.name())
            //Set up the translation call URL
            val yandexKey = Credentials.YANDEX_KEY
            val yandexUrl = ("https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                    + "&text=" + encodeText + "&lang=" + languagePair)
            val yandexTranslateURL = URL(yandexUrl)

            //Set Http Conncection, Input Stream, and Buffered Reader
            val httpJsonConnection = yandexTranslateURL.openConnection() as HttpURLConnection
            val inputStream = httpJsonConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))

            //Set string builder and insert retrieved JSON result into it
            val jsonStringBuilder = StringBuilder()
            while ({jsonString = bufferedReader.readLine(); jsonString}() != null) {
                jsonStringBuilder.append(jsonString + "\n")
            }

            //Close and disconnect
            bufferedReader.close()
            inputStream.close()
            httpJsonConnection.disconnect()

            //Making result human readable
            var resultString = jsonStringBuilder.toString().trim { it <= ' ' }
            //Getting the characters between [ and ]
            resultString = resultString.substring(resultString.indexOf('[') + 1)
            resultString = resultString.substring(0, resultString.indexOf("]"))
            //Getting the characters between " and "
            resultString = resultString.substring(resultString.indexOf("\"") + 1)
            resultString = resultString.substring(0, resultString.indexOf("\""))

//            Utils.createToast(context,"Translation Result:" + resultString)
            Log.d("Translation Result:", resultString)
            return jsonStringBuilder.toString().trim { it <= ' ' }

        } catch (e: Throwable) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}