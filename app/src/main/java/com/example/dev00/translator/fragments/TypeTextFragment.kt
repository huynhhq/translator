package com.example.dev00.translator.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.dev00.translator.R
import com.example.dev00.translator.adapters.ListSpeakTextViewAdapter
import com.example.dev00.translator.animator.FadeInDownAnimator
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.interfaces.IGoogle
import com.example.dev00.translator.interfaces.IYandex
import com.example.dev00.translator.models.AppData_Singleton
import com.example.dev00.translator.models.GoogleResponse
import com.example.dev00.translator.models.SpTextData
import com.example.dev00.translator.models.YandexResponse
import com.example.dev00.translator.services.ServiceManager
import com.example.dev00.translator.services.TTS
import com.example.dev00.translator.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_type_text.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypeTextFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var languageCodePair = ""
    private lateinit var appData_Singleton: AppData_Singleton
    private lateinit var listSpeakTextViewAdapter: ListSpeakTextViewAdapter
    private lateinit var translateService: Any

    private val TAG_TWO_VOICE = "TWO_VOICE"
    private var TEXT_DIRECTION = ""
    private val TAG_TEXT_LEFT = "TEXT_LEFT"
    private val TAG_TEXT_RIGHT = "TEXT_RIGHT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initalVar()
        return inflater.inflate(R.layout.fragment_type_text, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalListener()
        initalViewFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        fun newInstance(param: String) =
                TypeTextFragment().apply {
                    arguments = Bundle().apply {
                        TEXT_DIRECTION = param
                    }
                }
    }

    private fun initalVar() {
        appData_Singleton = AppData_Singleton.getInstance()

        listSpeakTextViewAdapter = ListSpeakTextViewAdapter(appData_Singleton.getAppData()!!.arrTranslateData, activity!!)

        translateService = ServiceManager.getService(appData_Singleton.getAppData()!!.appSettingData!!.translatedApi)
    }

    private fun initalListener() {
        Utils.imageEffect(iv_back_voice, activity!!)
        Utils.imageEffect(iv_done, activity!!)

        iv_back_voice.setOnClickListener {
            var f = activity!!.supportFragmentManager!!.findFragmentByTag(TEXT_DIRECTION)
            when (TEXT_DIRECTION) {
                TAG_TEXT_RIGHT -> {
                    activity!!.supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                            .replace(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
                            .commit()
                    activity!!.supportFragmentManager.beginTransaction().remove(f!!).commit()
                }
                TAG_TEXT_LEFT -> {
                    activity!!.supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                            .replace(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
                            .commit()
                    activity!!.supportFragmentManager.beginTransaction().remove(f!!).commit()
                }
            }
        }

        iv_done.setOnClickListener {
            var textTranslate = text_translate.text.toString()
            if (textTranslate.length != 0) {
                processRecognitionData(textTranslate)
                text_translate.text.clear()
                Utils.hideKeyboardFrom(activity!!, text_translate)
            }
        }
    }

    private fun initalViewFragment() {

        activity!!.main_rcv.layoutManager = LinearLayoutManager(activity!!)
        activity!!.main_rcv.setHasFixedSize(true)
        activity!!.main_rcv.itemAnimator = FadeInDownAnimator(null)
        activity!!.main_rcv.adapter = listSpeakTextViewAdapter

        when (TEXT_DIRECTION) {
            TAG_TEXT_LEFT -> {
                setLanguageCodePair(appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                        , appData_Singleton.getAppData()!!.rightFlag!!.languageCode)
                text_translate.hint = appData_Singleton.getAppData()!!.leftFlag!!.name
            }
            TAG_TEXT_RIGHT -> {
                setLanguageCodePair(appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                        , appData_Singleton.getAppData()!!.leftFlag!!.languageCode)
                text_translate.hint = appData_Singleton.getAppData()!!.rightFlag!!.name
            }
        }

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        activity!!.main_li.layoutParams.height = (displayMetrics.heightPixels
                - Utils.convertDpToPixel(50.toFloat(), activity!!).toInt()
                - activity!!.toolbar.layoutParams.height
                - Utils.getStatusBarHeight(activity!!))
    }

    private fun processRecognitionData(resultData: String) {
        var textL = ""
        var textR = ""
        var target = ""
        var localeStr = ""

        when (appData_Singleton.getAppData()!!.appSettingData!!.translatedApi) {
            Constants.YANDEX_API -> {
                var call = (translateService as IYandex).translate(Constants.YANDEX_KEY, resultData, this.languageCodePair)

                call.enqueue(object : Callback<YandexResponse> {
                    override fun onResponse(call: Call<YandexResponse>, response: Response<YandexResponse>) {
                        var translationResult = response.body().text[0]!!

                        when (TEXT_DIRECTION) {
                            TAG_TEXT_LEFT -> {
                                textL = resultData
                                textR = translationResult
                                localeStr = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                            }

                            TAG_TEXT_RIGHT -> {
                                textL = translationResult
                                textR = resultData
                                localeStr = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                            }
                        }

                        appData_Singleton.getAppData()!!
                                .arrTranslateData
                                .add(SpTextData(appData_Singleton.getAppData()!!.leftFlag
                                        , textL
                                        , appData_Singleton.getAppData()!!.rightFlag
                                        , textR))

                        listSpeakTextViewAdapter.setItemList(appData_Singleton
                                .getAppData()!!.arrTranslateData)
                        listSpeakTextViewAdapter.notifyItemInserted(appData_Singleton.getAppData()!!
                                .arrTranslateData.size - 1)
                        activity!!.main_rcv.scrollToPosition(appData_Singleton.getAppData()!!
                                .arrTranslateData.size - 1)
                        activity!!.main_rcv.itemAnimator?.apply {
                            addDuration = 350
                            removeDuration = 100
                            moveDuration = 350
                            changeDuration = 100
                        }

                        TTS(activity!!, translationResult, localeStr)
                    }

                    override fun onFailure(call: Call<YandexResponse>?, t: Throwable?) {
                        Utils.createToast(activity!!, Constants.CHECK_YOUR_INTERNET)
                    }
                })
            }

            Constants.GOOGLE_API -> {
                when (TEXT_DIRECTION) {
                    TAG_TEXT_RIGHT -> {
                        target = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    }

                    TAG_TEXT_LEFT -> {
                        target = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                    }
                }
                var call = (translateService as IGoogle).translate(Constants.GOOGLE_KEY, resultData, target)

                call.enqueue(object : Callback<GoogleResponse> {

                    override fun onResponse(call: Call<GoogleResponse>, response: Response<GoogleResponse>) {
                        var translationResult = response.body().data.translations[0].translatedText

                        when (TEXT_DIRECTION) {
                            TAG_TEXT_LEFT -> {
                                textL = resultData
                                textR = translationResult
                                localeStr = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                            }

                            TAG_TEXT_RIGHT -> {
                                textL = translationResult
                                textR = resultData
                                localeStr = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                            }
                        }

                        appData_Singleton.getAppData()!!
                                .arrTranslateData
                                .add(SpTextData(appData_Singleton.getAppData()!!.leftFlag
                                        , textL
                                        , appData_Singleton.getAppData()!!.rightFlag
                                        , textR))

                        listSpeakTextViewAdapter.setItemList(appData_Singleton.getAppData()!!
                                .arrTranslateData)
                        listSpeakTextViewAdapter.notifyItemInserted(appData_Singleton.getAppData()!!
                                .arrTranslateData.size - 1)
                        activity!!.main_rcv.scrollToPosition(appData_Singleton.getAppData()!!
                                .arrTranslateData.size - 1)
                        activity!!.main_rcv.itemAnimator?.apply {
                            addDuration = 350
                            removeDuration = 100
                            moveDuration = 350
                            changeDuration = 100
                        }

                        TTS(activity!!, translationResult, localeStr)
                    }

                    override fun onFailure(call: Call<GoogleResponse>?, t: Throwable?) {
                        Utils.createToast(activity!!, Constants.CHECK_YOUR_INTERNET)
                    }
                })
            }

        }
    }

    override fun onResume() {
        super.onResume()
        translateService = ServiceManager.getService(appData_Singleton.getAppData()!!.appSettingData.translatedApi)
    }

    private fun setLanguageCodePair(firstLanguageCode: String, secondLanguageCode: String) {
        this.languageCodePair = "$firstLanguageCode-$secondLanguageCode"
    }

}
