package com.example.dev00.translator.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.ImageView
import android.widget.ListView
import com.example.dev00.translator.R
import com.example.dev00.translator.adapters.FlagListViewAdapter
import com.example.dev00.translator.adapters.ListSpeakTextViewAdapter
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_voice_speak.*
import android.util.DisplayMetrics
import com.example.dev00.translator.activities.VoiceAnimationActivity
import com.example.dev00.translator.animator.FadeInDownAnimator
import com.example.dev00.translator.helpers.Constants.Companion.SPEECH_RECOGNITION_CODE
import com.example.dev00.translator.helpers.Credentials
import com.example.dev00.translator.interfaces.IGoogle
import com.example.dev00.translator.interfaces.IYandex
import com.example.dev00.translator.models.*
import com.example.dev00.translator.services.ServiceManager
import com.example.dev00.translator.services.TTS
import com.example.dev00.translator.utils.Utils.Companion.getStatusBarHeight
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VoiceSpeakFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var appData_Singleton: AppData_Singleton
    private var languageCodePair = ""
    private var MODE_SPEAK = Constants.LEFT_MODE
    private lateinit var translateService: Any
    private lateinit var msgToastApi: String

    private lateinit var adapterLeft: FlagListViewAdapter
    private lateinit var adapterRight: FlagListViewAdapter
    private lateinit var listSpeakTextViewAdapter: ListSpeakTextViewAdapter

    private lateinit var objGoogleFlags: JSONArray
    private lateinit var objYandexFlags: JSONArray

    private lateinit var listGoogleFlags: List<Flag>
    private lateinit var listYandexFlags: List<Flag>

    private val TAG_TWO_VOICE = "TWO_VOICE"
    private val TAG_TEXT_LEFT = "TEXT_LEFT"
    private val TAG_TEXT_RIGHT = "TEXT_RIGHT"
    private val REQUEST_CODE = 2
    private val TEXT_RESULT = "TEXT_RESULT"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        initalVar()
        return inflater.inflate(R.layout.fragment_voice_speak, container, false)
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
            throw RuntimeException(context.toString() + Constants.MUST_IMPLEMENT_ONFRAGMENTINTERRACTIONLISTENER)
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun openFlagDialog(context: Context, direction: Int, adapter: FlagListViewAdapter) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var row: View = inflater.inflate(R.layout.list_item_flag_dialog, null)
        var lv: ListView = row.findViewById(R.id.flags_listView)
        lv.adapter = adapter
        builder.setView(row)
        var dialog: AlertDialog = builder.create()
        dialog.show()
        lv.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as Flag
            var appData = appData_Singleton.getAppData()

            if (direction == Constants.LEFT_LANGUAGE) {
                appData!!.leftFlag = selectedItem
                changeFlag(selectedItem, img_left, context)
                dialog.dismiss()
            } else {
                appData!!.rightFlag = selectedItem
                changeFlag(selectedItem, img_right, context)
                dialog.dismiss()
            }
            adapter.setSelectedPosition(position)
            Utils.createToast(context, selectedItem.name)
        }
    }


    fun changeFlag(flag: Flag, img: ImageView, context: Context) {
        img.setImageBitmap(Utils.getBitMapFromAssets(context, flag.image))
        img.layoutParams.height = resources.getDimension(R.dimen.height_flag).toInt();
        img.layoutParams.width = resources.getDimension(R.dimen.width_flag).toInt();
    }


    /**
     * Start speech to text intent. This opens up Google Speech Recognition API dialog box to listen the speech input.
     */
    private fun startSpeechToText(context: Context, language: String, languageCode: String) {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageCode)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                language)
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE)
        } catch (a: ActivityNotFoundException) {
            var intent = Intent()
            intent.setClass(activity!!, VoiceAnimationActivity::class.java)
            intent.putExtra("MODE_SPEAK", MODE_SPEAK)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    /**
     * Callback for speech recognition activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SPEECH_RECOGNITION_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    var text = result[0]
                    processRecognitionData(text)
                }
            }
            REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        var text = data!!.getStringExtra(TEXT_RESULT)
                        processRecognitionData(text)
                    }
                    Activity.RESULT_CANCELED -> {

                    }
                }
            }
        }
    }

    private fun processRecognitionData(resultData: String) {
//        var translationResult = Utils.yandexTranslate(resultData, this.languageCodePair, activity!!)
        var textL = ""
        var textR = ""
        var target = ""
        var localeStr = ""

        when (appData_Singleton.getAppData()!!.appSettingData.translatedApi) {
            Constants.YANDEX_API -> {
                var call = (translateService as IYandex).translate(Credentials.YANDEX_KEY, resultData, this.languageCodePair)

                call.enqueue(object : Callback<YandexResponse> {
                    override fun onResponse(call: Call<YandexResponse>, response: Response<YandexResponse>) {
                        var translationResult = response.body()
                        if (translationResult != null) {
                            var textResult = translationResult.text[0]
                            when (MODE_SPEAK) {
                                0 -> {
                                    textL = resultData
                                    textR = textResult
                                    localeStr = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                                }

                                1 -> {
                                    textL = textResult
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

                            TTS(activity!!, textResult, localeStr)
                        } else {
                            Utils.createToast(activity!!, msgToastApi)
                        }
                    }

                    override fun onFailure(call: Call<YandexResponse>?, t: Throwable?) {
                        Utils.createToast(activity!!, Constants.CHECK_YOUR_INTERNET)
                    }
                })
            }

            Constants.GOOGLE_API -> {
                when (MODE_SPEAK) {
                    0 -> {
                        target = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                    }

                    1 -> {
                        target = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    }
                }
                var call = (translateService as IGoogle).translate(Credentials.GOOGLE_KEY, resultData, target)

                call.enqueue(object : Callback<GoogleResponse> {

                    override fun onResponse(call: Call<GoogleResponse>, response: Response<GoogleResponse>) {
                        var translationResult = response.body()
                        if (translationResult != null) {
                            var textResult = translationResult.data.translations[0].translatedText
                            when (MODE_SPEAK) {
                                0 -> {
                                    textL = resultData
                                    textR = textResult
                                    localeStr = appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                                }

                                1 -> {
                                    textL = textResult
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

                            TTS(activity!!, textResult, localeStr)
                        } else {
                            Utils.createToast(activity!!, msgToastApi)
                        }
                    }

                    override fun onFailure(call: Call<GoogleResponse>?, t: Throwable?) {
                        Utils.createToast(activity!!, Constants.CHECK_YOUR_INTERNET)
                    }
                })
            }

        }
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(): VoiceSpeakFragment {
            return VoiceSpeakFragment()
        }
    }

    private fun initalVar() {

        //Load data from json file

        //Google Flags
        val objGGFlag = JSONObject(Utils.loadJSONFromAsset(activity!!, Constants.JSON_RESOURCE_FILE_GOOGLE))
        objGoogleFlags = objGGFlag.getJSONArray(Constants.JSON_TYPE_GOOGLE_FLAGS)
        listGoogleFlags = Utils.parseToListFlag(objGoogleFlags)
        //End google flags

        //Yandex Flags
        val objYDFlag = JSONObject(Utils.loadJSONFromAsset(activity!!, Constants.JSON_RESOURCE_FILE_YANDEX))
        objYandexFlags = objYDFlag.getJSONArray(Constants.JSON_TYPE_YANDEX_FLAGS)
        listYandexFlags = Utils.parseToListFlag(objYandexFlags)
        //End Yandex flags

        //End load data from json file

        appData_Singleton = AppData_Singleton.getInstance()

        when (appData_Singleton.getAppData()!!.appSettingData.translatedApi) {
            Constants.YANDEX_API -> {
                adapterLeft = FlagListViewAdapter(activity!!, listYandexFlags)
                adapterLeft.setSelectedPosition(Utils.findIndexFlag(listYandexFlags, Utils.initalFromFlag()))

                adapterRight = FlagListViewAdapter(activity!!, listYandexFlags)
                adapterRight.setSelectedPosition(Utils.findIndexFlag(listYandexFlags, Utils.initalToFlag()))
            }

            Constants.GOOGLE_API -> {
                adapterLeft = FlagListViewAdapter(activity!!, listGoogleFlags)
                adapterLeft.setSelectedPosition(Utils.findIndexFlag(listGoogleFlags, Utils.initalFromFlag()))

                adapterRight = FlagListViewAdapter(activity!!, listGoogleFlags)
                adapterRight.setSelectedPosition(Utils.findIndexFlag(listGoogleFlags, Utils.initalToFlag()))
            }
        }

        listSpeakTextViewAdapter = ListSpeakTextViewAdapter(appData_Singleton.getAppData()!!.arrTranslateData, activity!!)

        //Inital Service for Yandex
        translateService = ServiceManager.getService(appData_Singleton.getAppData()!!.appSettingData.translatedApi)
        //End inital service for Yandex

    }

    private fun initalListener() {
        Utils.imageEffect(iv_keyboard_left, activity!!)
        Utils.imageEffect(iv_keyboard_right, activity!!)

        Utils.imageEffect(img_left, activity!!)
        Utils.imageEffect(img_right, activity!!)

        Utils.imageChangeEffect(img_voice_left, R.drawable.microphone, R.drawable.microphone_down, activity!!)
        Utils.imageChangeEffect(img_voice_right, R.drawable.microphone, R.drawable.microphone_down, activity!!)

        img_left.setOnClickListener({
            openFlagDialog(activity!!, Constants.LEFT_LANGUAGE, adapterLeft)
        })

        img_right.setOnClickListener({
            openFlagDialog(activity!!, Constants.RIGHT_LANGUAGE, adapterRight)
        })

        img_voice_left.setOnClickListener({
            var language = appData_Singleton.getAppData()!!.leftFlag

            setLanguageCodePair(appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    , appData_Singleton.getAppData()!!.rightFlag!!.languageCode)

            this.MODE_SPEAK = Constants.LEFT_MODE

            startSpeechToText(activity!!, language!!.name, language.languageCode)
        })

        img_voice_right.setOnClickListener({
            var language = appData_Singleton.getAppData()!!.rightFlag

            setLanguageCodePair(appData_Singleton.getAppData()!!.rightFlag!!.languageCode
                    , appData_Singleton.getAppData()!!.leftFlag!!.languageCode)

            this.MODE_SPEAK = Constants.RIGHT_MODE

            startSpeechToText(activity!!, language!!.name, language.languageCode)
        })

        iv_keyboard_left.setOnClickListener {
            var f = activity!!.supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
            activity!!.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                    .replace(R.id.bottom_layout, TypeTextFragment.newInstance(TAG_TEXT_LEFT), TAG_TEXT_LEFT)
                    .commit()
            activity!!.supportFragmentManager.beginTransaction().remove(f!!).commit()
        }

        iv_keyboard_right.setOnClickListener {
            var f = activity!!.supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
            activity!!.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                    .replace(R.id.bottom_layout, TypeTextFragment.newInstance(TAG_TEXT_RIGHT), TAG_TEXT_RIGHT)
                    .commit()
            activity!!.supportFragmentManager.beginTransaction().remove(f!!).commit()
        }
    }

    private fun initalViewFragment() {

        changeFlag(appData_Singleton.getAppData()!!.leftFlag!!, img_left, activity!!)
        changeFlag(appData_Singleton.getAppData()!!.rightFlag!!, img_right, activity!!)

        activity!!.main_rcv.layoutManager = LinearLayoutManager(activity!!)
        activity!!.main_rcv.setHasFixedSize(true)
        activity!!.main_rcv.itemAnimator = FadeInDownAnimator(null)
        activity!!.main_rcv.adapter = listSpeakTextViewAdapter

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        activity!!.main_li.layoutParams.height = (displayMetrics.heightPixels
                - Utils.convertDpToPixel(100.toFloat(), activity!!).toInt()
                - activity!!.toolbar.layoutParams.height
                - getStatusBarHeight(activity!!))
    }

    private fun setLanguageCodePair(firstLanguageCode: String, secondLanguageCode: String) {
        this.languageCodePair = "$firstLanguageCode-$secondLanguageCode"
    }

    override fun onResume() {
        super.onResume()
        changeListFlagAfterChange()
        updateToastApi()
    }

    private fun changeListFlagAfterChange() {
        translateService = ServiceManager.getService(appData_Singleton.getAppData()!!.appSettingData.translatedApi)

        var lastPosLeft = adapterLeft.selected_position
        var lastPosRight = adapterRight.selected_position

        when (appData_Singleton.getAppData()!!.appSettingData.translatedApi) {
            Constants.YANDEX_API -> {
                adapterLeft = FlagListViewAdapter(activity!!, listYandexFlags)
                adapterLeft.setSelectedPosition(lastPosLeft)

                adapterRight = FlagListViewAdapter(activity!!, listYandexFlags)
                adapterRight.setSelectedPosition(lastPosRight)

                appData_Singleton.getAppData()!!.leftFlag = listYandexFlags[lastPosLeft]
                appData_Singleton.getAppData()!!.rightFlag = listYandexFlags[lastPosRight]
            }

            Constants.GOOGLE_API -> {
                adapterLeft = FlagListViewAdapter(activity!!, listGoogleFlags)
                adapterLeft.setSelectedPosition(lastPosLeft)

                adapterRight = FlagListViewAdapter(activity!!, listGoogleFlags)
                adapterRight.setSelectedPosition(lastPosRight)

                appData_Singleton.getAppData()!!.leftFlag = listGoogleFlags[lastPosLeft]
                appData_Singleton.getAppData()!!.rightFlag = listGoogleFlags[lastPosRight]
            }
        }
    }

    private fun updateToastApi() {
        when (appData_Singleton.getAppData()!!.appSettingData.languageApp) {
            Constants.LANGUAGE_EN -> {
                msgToastApi = activity!!.resources.getString(R.string.pl_en)
            }
            Constants.LANGUAGE_VN -> {
                msgToastApi = activity!!.resources.getString(R.string.pl_vn)
            }
            Constants.LANGUAGE_JP -> {
                msgToastApi = activity!!.resources.getString(R.string.pl_jp)
            }
        }
    }
}
