package com.example.dev00.translator.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
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
import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Matrix
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.example.dev00.translator.BuildConfig
import com.example.dev00.translator.R


class Utils {
    companion object {

        @JvmStatic
        fun writeLog(tag: String?, msg: String?) = Log.d(tag, msg)

        @JvmStatic
        fun createToast(context: Context
                        , message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

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
            var flagResult = Flag(Constants.FROM_NAME, Constants.FROM_IMG_PATH, Constants.FROM_LANGUAGE_CODE_YD, Constants.FROM_ASR_CODE)
            return flagResult
        }

        @JvmStatic
        fun initalToFlag(): Flag {
            var flagResult = Flag(Constants.TO_NAME, Constants.TO_IMG_PATH, Constants.TO_LANGUAGE_CODE_YD, Constants.TO_ASR_CODE)
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
                                , jsonArray.getJSONObject(i).getString(Constants.JSON_FIELD_LANGUAGE_CODE)
                                , jsonArray.getJSONObject(i).getString(Constants.JSON_FIELD_ASR_CODE)))
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return list
        }

        @JvmStatic
        fun findIndexFlag(listFlags: List<Flag>, selectedFlag: Flag): Int {
            var index = 0
            for (i in 0 until listFlags.size) {
                if (listFlags.get(i).equals(selectedFlag)) {
                    break
                }
                index++
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

        @JvmStatic
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
        }

        @JvmStatic
        fun buttonEffect(button: View) {
            button.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.background.setColorFilter(-0x1f0b8adf, PorterDuff.Mode.SRC_ATOP)
                        v.invalidate()
                    }
                    MotionEvent.ACTION_UP -> {
                        v.background.clearColorFilter()
                        v.invalidate()
                    }
                }
                false
            }
        }

        @JvmStatic
        fun imageEffect(imageView: ImageView, context: Context){
            imageView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, m: MotionEvent): Boolean {
                    when(m.action){
                        MotionEvent.ACTION_DOWN -> {
                            imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.md_yellow_900))
                            return false
                        }
                        MotionEvent.ACTION_UP -> {
                            imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.md_yellow_800))
                            return false
                        }
                    }
                    return true
                }
            })
        }

        @JvmStatic
        fun imageChangeEffect(imageView: ImageView,imageViewUp: Int, imageViewDown: Int, context: Context){
            imageView.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, m: MotionEvent): Boolean {
                    when(m.action){
                        MotionEvent.ACTION_DOWN -> {
                            imageView.setImageDrawable(ContextCompat.getDrawable(context, imageViewDown))
                            return false
                        }
                        MotionEvent.ACTION_UP -> {
                            imageView.setImageDrawable(ContextCompat.getDrawable(context, imageViewUp))
                            return false
                        }
                    }
                    return true
                }
            })
        }

        @JvmStatic
        fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)

            // "RECREATE" THE NEW BITMAP
            val resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false)
            bm.recycle()
            return resizedBitmap
        }

        @JvmStatic
        private fun updateResources(context: Context, language: String) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val config = Configuration()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setSystemLocale(config, locale);
            }else{
                setSystemLocaleLegacy(config, locale);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.createConfigurationContext(config);
            } else {
                context.resources.updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        }

        @SuppressWarnings("deprecation")
        private fun getSystemLocaleLegacy(config: Configuration): Locale{
            return config.locale;
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        @SuppressWarnings("deprecation")
        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }

    }
}