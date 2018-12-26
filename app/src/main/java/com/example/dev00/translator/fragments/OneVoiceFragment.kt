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
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.dev00.translator.R
import com.example.dev00.translator.adapters.FlagListViewAdapter
import com.example.dev00.translator.adapters.ListSpeakTextViewAdapter
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.models.*
import com.example.dev00.translator.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_one_voice.*
import org.json.JSONArray
import org.json.JSONObject

class OneVoiceFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var adapterLeft: FlagListViewAdapter

    private lateinit var adapterRight: FlagListViewAdapter

    private lateinit var listSpeakTextViewAdapter: ListSpeakTextViewAdapter

    private lateinit var appData_Singleton: AppData_Singleton

    private lateinit var arrData: ArrayList<SpTextData>

    private lateinit var objGoogleFlags: JSONArray

    private lateinit var objYandexFlags: JSONArray

    private lateinit var listGoogleFlags: List<Flag>

    private lateinit var listYandexFlags: List<Flag>

    private var languageCodePair = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initalVar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_voice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalListener()

        initalViewFragment()
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
                changeFlag(selectedItem, one_img_left, context)
                dialog.dismiss()
            } else {
                appData!!.rightFlag = selectedItem
                changeFlag(selectedItem, one_img_right, context)
                dialog.dismiss()
            }
            adapter.setSelectedPosition(position)
            Utils.createToast(context, selectedItem.name)
        }
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
            startActivityForResult(intent, Constants.SPEECH_RECOGNITION_CODE)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(context,
                    Constants.SPEECH_RECOGNITION_NOT_SUPPORT,
                    Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Callback for speech recognition activity
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.SPEECH_RECOGNITION_CODE -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val text = result[0]
                    val leftFlag = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    val rightFlag = appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    val languagePair = "$leftFlag-$rightFlag"
                    var tranlateResult = Utils.yandexTranslate(text, languagePair, activity!!)
                    arrData.add(SpTextData(appData_Singleton.getAppData()!!.leftFlag
                            , text
                            , appData_Singleton.getAppData()!!.rightFlag
                            , tranlateResult))
                    listSpeakTextViewAdapter.setItemList(arrData)
                    listSpeakTextViewAdapter.notifyDataSetChanged()
                    activity!!.main_rcv.scrollToPosition(arrData.size - 1)
                }
            }
        }
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance(): OneVoiceFragment {
            return OneVoiceFragment()
        }
    }

    fun changeFlag(flag: Flag, img: ImageView, context: Context) {
        img!!.setImageBitmap(Utils.getBitMapFromAssets(context, flag.image))
        img.layoutParams.height = resources.getDimension(R.dimen.height_flag).toInt();
        img.layoutParams.width = resources.getDimension(R.dimen.width_flag).toInt();
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

        arrData = arrayListOf()

        //Todo: Change list flag here
        adapterLeft = FlagListViewAdapter(activity!!, listYandexFlags)

        adapterRight = FlagListViewAdapter(activity!!, listYandexFlags)

        listSpeakTextViewAdapter = ListSpeakTextViewAdapter(arrData, activity!!)

    }

    private fun initalListener() {
        one_img_left.setOnClickListener({
            openFlagDialog(activity!!, Constants.LEFT_LANGUAGE, adapterLeft)
        })

        one_img_right.setOnClickListener({
            openFlagDialog(activity!!, Constants.RIGHT_LANGUAGE, adapterRight)
        })

        img_voice_middle.setOnClickListener({
            var language = appData_Singleton.getAppData()!!.leftFlag

            setLanguageCodePair(appData_Singleton.getAppData()!!.leftFlag!!.languageCode
                    , appData_Singleton.getAppData()!!.rightFlag!!.languageCode)

            startSpeechToText(activity!!, language!!.name, language!!.languageCode)
        })
    }

    private fun initalViewFragment() {

        if (appData_Singleton.getAppData() == null) {
            appData_Singleton.setAppData(AppData(Utils.initalFromFlag()
                    , Utils.initalToFlag()
                    , Constants.YANDEX_API
                    , Constants.LANGUAGE_ENGLISH))
        }
        changeFlag(appData_Singleton!!.getAppData()!!.leftFlag!!, one_img_left, activity!!)
        changeFlag(appData_Singleton!!.getAppData()!!.rightFlag!!, one_img_right, activity!!)

        activity!!.main_rcv.layoutManager = LinearLayoutManager(activity!!)
        activity!!.main_rcv.setHasFixedSize(true)
        activity!!.main_rcv.itemAnimator = DefaultItemAnimator()
        activity!!.main_rcv.addItemDecoration(DividerItemDecoration(activity!!.main_rcv.context
                , DividerItemDecoration.VERTICAL))
        activity!!.main_rcv.adapter = listSpeakTextViewAdapter


        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        activity!!.main_li.layoutParams.height = (displayMetrics.heightPixels
                - Utils.convertDpToPixel(100.toFloat(), activity!!).toInt()
                - activity!!.toolbar.layoutParams.height
                - Utils.getStatusBarHeight(activity!!))
    }

    private fun setLanguageCodePair(firstLanguageCode: String, secondLanguageCode: String) {
        this.languageCodePair = "$firstLanguageCode-$secondLanguageCode"
    }
}
