package com.example.dev00.translator.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.dev00.translator.R
import com.example.dev00.translator.models.AppData_Singleton
import com.example.dev00.translator.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_type_text.*

class TypeTextFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var appData_Singleton: AppData_Singleton

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
    }

    private fun initalListener() {
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
    }

    private fun initalViewFragment() {

        when (TEXT_DIRECTION){
            TAG_TEXT_LEFT -> {
                text_translate.hint = appData_Singleton.getAppData()!!.leftFlag!!.name
            }
            TAG_TEXT_RIGHT -> {
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
}
