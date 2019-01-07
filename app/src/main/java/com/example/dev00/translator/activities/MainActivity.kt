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
import com.example.dev00.translator.R.id.*
import com.example.dev00.translator.fragments.OneVoiceFragment
import com.example.dev00.translator.fragments.TypeTextFragment
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.utils.Utils
import com.example.dev00.translator.models.*
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), VoiceSpeakFragment.OnFragmentInteractionListener
        , OneVoiceFragment.OnFragmentInteractionListener, TypeTextFragment.OnFragmentInteractionListener {

    private lateinit var context: Context
    val gson = Gson()
    private var doubleBackToExitPressedOnce: Boolean = false
    private lateinit var appData_Singleton: AppData_Singleton
    private var prefs: Prefs? = null
    private var menu: Menu? = null

    private lateinit var appSettingData: AppSettingData
    private val TAG_ONE_VOICE = "ONE_VOICE"
    private val TAG_TWO_VOICE = "TWO_VOICE"
    private val TAG_TEXT_LEFT = "TEXT_LEFT"
    private val TAG_TEXT_RIGHT = "TEXT_RIGHT"

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
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            action_settings -> {
                var intent = Intent()
                intent.setClass(this@MainActivity, SettingActivity::class.java)
                startActivity(intent)
                return true
            }

            action_about_us -> {
                var intent = Intent()
                intent.setClass(this@MainActivity, AboutUsActivity::class.java)
                startActivity(intent)
                return true
            }

            action_clear -> {
                appData_Singleton.getAppData()!!.arrTranslateData.clear()
                main_rcv.adapter!!.notifyDataSetChanged()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFragmentInteraction(uri: Uri) {

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

                        }
                        else -> {
                            supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left)
                                    .replace(R.id.bottom_layout, TypeTextFragment.newInstance(TAG_TEXT_RIGHT), TAG_TEXT_RIGHT)
                                    .commit()
                            supportFragmentManager.beginTransaction().remove(f!!).commit()

                        }
                    }
                }
                return false
            }

            override fun onSwipeRight(fingers: Int, gestureDuration: Long, gestureDistance: Double): Boolean {
                if (fingers == 1) {
                    var f = supportFragmentManager!!.findFragmentByTag(TAG_TWO_VOICE)
                    when (f) {
                        null -> {

                        }
                        else -> {
                            supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right)
                                    .replace(R.id.bottom_layout, TypeTextFragment.newInstance(TAG_TEXT_LEFT), TAG_TEXT_LEFT)
                                    .commit()
                            supportFragmentManager.beginTransaction().remove(f!!).commit()

                        }
                    }
                }
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
        prefs = Prefs(this)
        var json = prefs!!.appSettingData
        if (json != null) {
            appSettingData = gson.fromJson(json, AppSettingData::class.java)
        } else {
            appSettingData = AppSettingData(Constants.YANDEX_API, Constants.LANGUAGE_EN)
        }
        appData_Singleton = AppData_Singleton.getInstance()

        var arrData: ArrayList<SpTextData> = arrayListOf()
        if (appData_Singleton.getAppData() == null) {
            appData_Singleton.setAppData(AppData(Utils.initalFromFlag()
                    , Utils.initalToFlag()
                    , appSettingData
                    , arrData))
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

    private fun prepareMenuView(menu: Menu) {
        when (appData_Singleton.getAppData()!!.appSettingData!!.languageApp) {
            Constants.LANGUAGE_EN -> {
                var settingMenu = menu.findItem(action_settings)
                var aboutUsMenu = menu.findItem(action_about_us)
                settingMenu.title = context.resources.getString(R.string.setting_menu_en)
                aboutUsMenu.title = context.resources.getString(R.string.about_us_menu_en)
            }
            Constants.LANGUAGE_VN -> {
                var settingMenu = menu.findItem(action_settings)
                var aboutUsMenu = menu.findItem(action_about_us)
                settingMenu.title = context.resources.getString(R.string.setting_menu_vn)
                aboutUsMenu.title = context.resources.getString(R.string.about_us_menu_vn)
            }
            Constants.LANGUAGE_JP -> {
                var settingMenu = menu.findItem(action_settings)
                var aboutUsMenu = menu.findItem(action_about_us)
                settingMenu.title = context.resources.getString(R.string.setting_menu_jp)
                aboutUsMenu.title = context.resources.getString(R.string.about_us_menu_jp)
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu!!
        prepareMenuView(menu!!)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        if(menu != null)
        prepareMenuView(menu!!)
    }

}
