package com.example.dev00.translator.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.dev00.translator.R
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.view.*
import com.example.dev00.translator.fragments.VoiceSpeakFragment
import `in`.championswimmer.sfg.lib.SimpleFingerGestures
import android.content.Context
import android.content.Intent
import android.os.Handler
import com.example.dev00.translator.fragments.OneVoiceFragment
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.models.AppData
import com.example.dev00.translator.models.AppData_Singleton
import com.example.dev00.translator.utils.Utils

class MainActivity : AppCompatActivity(), VoiceSpeakFragment.OnFragmentInteractionListener
        , OneVoiceFragment.OnFragmentInteractionListener {

    private lateinit var context: Context

    private val TAG_ONE_VOICE = "ONE_VOICE"

    private val TAG_TWO_VOICE = "TWO_VOICE"

    private val TAG_TEXT_LEFT = "TEXT_LEFT"

    private val TAG_TEXT_RIGHT = "TEXT_RIGHT"

    private var doubleBackToExitPressedOnce: Boolean = false

    private lateinit var appData_Singleton: AppData_Singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this

        setupToolbar()

        initalAppData()

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
                    .commit()
        }

        setupSimpleFingerGestures()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_settings -> {
                var intent = Intent()
                intent.setClass(this@MainActivity, SettingActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.action_about_us -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setupSimpleFingerGestures() {
        val mySfg = SimpleFingerGestures()
        mySfg.setDebug(true);
        mySfg.setConsumeTouchEvents(true);
        mySfg.setOnFingerGestureListener(object : SimpleFingerGestures.OnFingerGestureListener {
            override fun onSwipeUp(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                //Todo: talk one for two languages (Pending)
//                if (fingers == 1) {
//                    var f = supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
//                    when (f) {
//                        null -> {
//                            supportFragmentManager
//                                    .beginTransaction()
//                                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
//                                    .replace(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
//                                    .commit()
//                            f = supportFragmentManager!!.findFragmentByTag(TAG_ONE_VOICE)
//                            supportFragmentManager.beginTransaction().remove(f!!).commit()
//
//                        }
//                        else -> {
//                            supportFragmentManager
//                                    .beginTransaction()
//                                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
//                                    .replace(R.id.bottom_layout, OneVoiceFragment.newInstance(), TAG_ONE_VOICE)
//                                    .commit()
//                            supportFragmentManager.beginTransaction().remove(f!!).commit()
//
//                        }
//                    }
//                }
                return false
            }

            override fun onSwipeDown(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                //Todo: talk one for two languages (Pending)
//                if (fingers == 1) {
//                    var f = supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
//                    when (f) {
//                        null -> {
//                            supportFragmentManager
//                                    .beginTransaction()
//                                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
//                                    .replace(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
//                                    .commit()
//                            f = supportFragmentManager!!.findFragmentByTag(TAG_ONE_VOICE)
//                            supportFragmentManager.beginTransaction().remove(f!!).commit()
//
//                        }
//                        else -> {
//                            supportFragmentManager
//                                    .beginTransaction()
//                                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_down)
//                                    .replace(R.id.bottom_layout, OneVoiceFragment.newInstance(), TAG_ONE_VOICE)
//                                    .commit()
//                            supportFragmentManager.beginTransaction().remove(f!!).commit()
//
//                        }
//                    }
//                }
                return false
            }

            override fun onSwipeLeft(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                if (fingers == 1) {
                    var f = supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
                    when (f) {
                        null -> {
                            supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                                    .replace(R.id.bottom_layout, VoiceSpeakFragment.newInstance(), TAG_TWO_VOICE)
                                    .commit()
                            f = supportFragmentManager!!.findFragmentByTag(TAG_ONE_VOICE)
                            supportFragmentManager.beginTransaction().remove(f!!).commit()

                        }
                        else -> {
                            supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                                    .replace(R.id.bottom_layout, OneVoiceFragment.newInstance(), TAG_ONE_VOICE)
                                    .commit()
                            supportFragmentManager.beginTransaction().remove(f!!).commit()

                        }
                    }
                }
                return false
            }

            override fun onSwipeRight(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                return false
            }

            override fun onPinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                return false
            }

            override fun onUnpinch(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                return false
            }

            override fun onDoubleTap(fingers: Int): Boolean {
                return false
            }
        })

        root_layout.setOnTouchListener(mySfg)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = Constants.TITLE_TOOLBAR

        // Display the app icon in action bar/toolbar
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setLogo(R.mipmap.ic_launcher)
        actionBar.setDisplayUseLogoEnabled(true)

    }

    private fun initalAppData() {
        appData_Singleton = AppData_Singleton.getInstance()

        if (appData_Singleton.getAppData() == null) {
            appData_Singleton.setAppData(AppData(Utils.initalFromFlag()
                    , Utils.initalToFlag()
                    , Constants.YANDEX_API
                    , Constants.LANGUAGE_ENGLISH))
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            finish()
            moveTaskToBack(true)
            System.exit(0)
        } else {
            doubleBackToExitPressedOnce = true
            Utils.createToast(this@MainActivity, "Nhấn Back thêm lần nữa để thoát")
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

}
