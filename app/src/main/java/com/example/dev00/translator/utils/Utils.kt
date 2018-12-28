package com.example.dev00.translator.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import com.example.dev00.translator.models.Flag
import java.io.InputStream
import android.util.DisplayMetrics
import android.widget.TextView
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.models.YandexResponse
import com.example.dev00.translator.services.YandexTranslatorBackground
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


class Utils {
    companion object {
        /**
         * HuynhHQ
         * Write Log
         */
        @JvmStatic
        fun writeLog(tag: String?, msg: String?) = Log.d(tag, msg)

        @JvmStatic
        fun createToast(context: Context
                        , message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()


//        @JvmStatic
//        fun getListFlags(): Array<Flag>{
//            var listView = arrayOf<Flag>( Flag("Arabic (Egypt)","flags/EGY.png", "ar-EG")
//                    , Flag("Arabic (International)","flags/XWW.png", "ar-AE")
//                    , Flag("Arabic (Saudi Arabia)","flags/SAU.png", "ar-SA")
//                    , Flag("Bengali","flags/BGD.png", "bn-BD")
//                    , Flag("Cambodia","flags/KHM.png", "km-KH")
//                    , Flag("Calatan","flags/ESP.png", "ca")
//                    , Flag("Chinese (Cantonese)","flags/CHN2.png", "yue-Hant-HK")
//                    , Flag("Chinese (Simplified)","flags/CHN.png", "cmn-Hans-CN")
//                    , Flag("Chinese (Tradional)","flags/TWN.png", "cmn-Hant-TW")
//                    , Flag("Croatian","flags/HRV.png", "hr-HR")
//                    , Flag("Czech","flags/CZE.png", "cs-CZ")
//                    , Flag("Danish","flags/DNK.png", "da-DK")
//                    , Flag("Dutch","flags/NLD.png", "nl-NL")
//                    , Flag("English (AUS)","flags/AUS.png", "en-AU")
//                    , Flag("English (India)", "flags/IND.png", "en-IN")
//                    , Flag("English (UK)","flags/GBR.png", "en-GB")
//                    , Flag("English (USA)","flags/USA.png", "en-US")
//                    , Flag("Filipino","flags/PHL.png", "fil-PH")
//                    , Flag("Finnish","flags/FIN.png", "fi-FI")
//                    , Flag("French (Canada)","flags/CAN.png", "fr-CA")
//                    , Flag("French (France)","flags/FRA.png", "fr-FR")
//                    , Flag("German","flags/DEU.png", "de-DE")
//                    , Flag("Greek","flags/GRC.png", "el-GR")
//                    , Flag("Hebrew","flags/ISR.png", "he-IL")
//                    , Flag("Hindi","flags/IND.png", "hi-IN")
//                    , Flag("Hungarian","flags/HUN.png", "hu-HU")
//                    , Flag("Indonesian","flags/IDN.png", "id-ID")
//                    , Flag("Italian","flags/ITA.png", "it-IT")
//                    , Flag("Japanese","flags/JPN.png", "ja-JP")
//                    , Flag("Korean","flags/KOR.png", "ko-KR")
//                    , Flag("Latvian","flags/LVN.png", "lv-LV")
//                    , Flag("Nepali","flags/NPL.png", "ne-NP")
//                    , Flag("Norwegian","flags/NOR.png", "nb-NO")
//                    , Flag("Polish","flags/POL.png", "pl-PL")
//                    , Flag("Portuguese (Brazil)","flags/BRA.png", "pt-BR")
//                    , Flag("Portuguese (Portugal)","flags/PRT.png", "pt-PT")
//                    , Flag("Romanian","flags/ROU.png", "ro-RO")
//                    , Flag("Russian","flags/RUS.png", "ru-RU")
//                    , Flag("Sinhala","flags/LKA.png", "si-LK")
//                    , Flag("Slovak","flags/SVK.png", "sk-SK")
//                    , Flag("Spanish (Latin America)","flags/XLA.png", "es-AR")
//                    , Flag("Spanish (Spain)","flags/ESP.png", "es-ES")
//                    , Flag("Swedish","flags/SWE.png", "sv-SE")
//                    , Flag("Tamil","flags/IND.png", "ta-IN")
//                    , Flag("Thai","flags/THA.png", "th-TH")
//                    , Flag("Turkish","flags/TUR.png", "tr-TR")
//                    , Flag("Ukrainian","flags/UKR.png", "uk-UA")
//                    , Flag("Vietnamese","flags/VNM.png", "vi-VN")
//            )
//
//            return listView
//        }

        @JvmStatic
        fun getBitMapFromAssets(context: Context, filePath: String): Bitmap? {
            var stream: InputStream? = null
            try {
                stream = context.assets.open(filePath)
                return BitmapFactory.decodeStream(stream)
            } catch (ignored: Exception) {
            } finally {
                try {
                    stream?.close()
                } catch (ignored: Exception) {
                }

            }
            return null
        }

        @JvmStatic
        fun convertDpToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }

        @JvmStatic
        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        @JvmStatic
        fun yandexTranslate(textToBeTranslated: String
                            , languagePair: String
                            , context: Context): String {
            val translatorBackgroundTask = YandexTranslatorBackground(context)
            var translationResult = translatorBackgroundTask.execute(textToBeTranslated, languagePair)
            val gson = Gson()
            val data: YandexResponse = gson.fromJson(translationResult.get(), YandexResponse::class.java)
            return data.text.get(0)
        }

        @JvmStatic
        fun loadJSONFromAsset(context: Context, filePath: String): String? {
            var json: String?
            try {
                val inputStream = context.assets.open(filePath)

                val size = inputStream.available()

                val buffer = ByteArray(size)

                inputStream.read(buffer)

                inputStream.close()

                json = String(buffer, Charset.forName("UTF-8"))

            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }

            return json

        }

        @JvmStatic
        fun initalFromFlag(): Flag {
            var flagResult = Flag(Constants.FROM_NAME, Constants.FROM_IMG_PATH, Constants.FROM_LANGUAGE_CODE_YD)
            return flagResult
        }

        @JvmStatic
        fun initalToFlag(): Flag {
            var flagResult = Flag(Constants.TO_NAME, Constants.TO_IMG_PATH, Constants.TO_LANGUAGE_CODE_YD)
            return flagResult
        }

        @JvmStatic
        fun parseToListFlag(jsonArray: JSONArray?): List<Flag> {
            val list = ArrayList<Flag>()
            try {
                if (jsonArray != null && jsonArray.length() > 0) {

                    for (i in 0 until jsonArray.length()) {
                        list.add(Flag(jsonArray.getJSONObject(i).getString(Constants.JSON_FIELD_NAME)
                                , jsonArray.getJSONObject(i).getString(Constants.JSON_FIELD_IMAGE)
                                , jsonArray.getJSONObject(i).getString(Constants.JSON_FIELD_LANGUAGE_CODE)))
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return list
        }

        @JvmStatic
        fun findIndexFlag(listFlags: List<Flag>, selectedFlag: Flag): Int {
            var index = 0;
            for (i in 0 until listFlags.size) {
                if (listFlags.get(i).equals(selectedFlag)) {
                    break
                }
                index++;
            }
            return index
        }

        @JvmStatic
        fun setTvZoomInOutAnimation(textView: TextView, nextTextView: TextView) {

            textView.isClickable = false
            nextTextView.isClickable = false

            val startWidth = 0f
            val endWidth = nextTextView.layoutParams.width.toFloat()

            val startHeight = 0f
            val endHeight = nextTextView.layoutParams.height.toFloat()

            val startSize = 30f
            val endSize = 15f
            val animationDuration = 1500 // Animation duration in ms
            val animator = ValueAnimator.ofFloat(startSize, endSize)
            animator.duration = animationDuration.toLong()

            val animatorTv = ValueAnimator.ofFloat(startWidth, endWidth)
            animatorTv.duration = animationDuration.toLong()

            val animatorTvH = ValueAnimator.ofFloat(startHeight, endHeight)
            animatorTvH.duration = animationDuration.toLong()

            animator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                textView.textSize = animatedValue
            }

            animatorTv.addUpdateListener { valueAnimator ->
                val animatedValueTv = valueAnimator.animatedValue as Float
                nextTextView.layoutParams.width = animatedValueTv.toInt()
            }

            animatorTvH.addUpdateListener { valueAnimator ->
                val animatedValueTvH = valueAnimator.animatedValue as Float
                nextTextView.layoutParams.height = animatedValueTvH.toInt()
            }

            animatorTv.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    textView.isClickable = true
                    nextTextView.isClickable = true
                }
            })

            animatorTv.start()
            animatorTvH.start()
            animator.start()
        }

    }
}