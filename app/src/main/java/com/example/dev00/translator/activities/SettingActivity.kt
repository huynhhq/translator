package com.example.dev00.translator.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.Constants
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatDrawableManager
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.dev00.translator.models.AppData_Singleton
import com.example.dev00.translator.models.Prefs
import com.example.dev00.translator.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private lateinit var appData_Singleton: AppData_Singleton
    private var prefs: Prefs? = null
    val gson = Gson()
    private lateinit var changeSettingMsg: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initalVar()
        setupToolbar()
        initalListener()
    }

    private fun setupToolbar(){
        setSupportActionBar(setting_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar!!

        // Set toolbar title/app title
        when(appData_Singleton.getAppData()!!.appSettingData.languageApp){
            Constants.LANGUAGE_JP -> {
                actionBar.title = this.resources.getString(R.string.toolbar_setting_jp)
            }
            Constants.LANGUAGE_VN -> {
                actionBar.title = this.resources.getString(R.string.toolbar_setting_vn)
            }
            Constants.LANGUAGE_EN -> {
                actionBar.title = this.resources.getString(R.string.toolbar_setting_en)
            }
        }
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //change color of back arrow, use AppCompatDrawableManager for pre-Lollipop version
        val backArrow = AppCompatDrawableManager.get().getDrawable(this ,R.drawable.ic_arrow_back_black_24dp)
        backArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        setting_toolbar.setNavigationOnClickListener({
            onBackPressed()
        })
    }

    private fun initalListener(){
        rd_api_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, id ->
            val radio: RadioButton = findViewById(id)
            when(radio.text){
                "Google API" -> {
                    appData_Singleton.getAppData()!!.appSettingData.translatedApi = Constants.GOOGLE_API
                }
                "Yandex API" -> {
                    appData_Singleton.getAppData()!!.appSettingData.translatedApi = Constants.YANDEX_API
                }
            }
            var json = gson.toJson(appData_Singleton.getAppData()!!.appSettingData)
            prefs!!.appSettingData = json
            Utils.createToast(this, "Changed successfully.")
        })

        rd_language_group.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, id ->
            val radio: RadioButton = findViewById(id)
            when(radio.text){
                "Vietnamese" -> {
                    appData_Singleton.getAppData()!!.appSettingData.languageApp = Constants.LANGUAGE_VN
                    tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_vn)
                    tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_vn)
                    changeSettingMsg = this.resources.getString(R.string.change_setting_msg_vn)
                    setupToolbar()
                }
                "English" -> {
                    appData_Singleton.getAppData()!!.appSettingData.languageApp = Constants.LANGUAGE_EN
                    tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_en)
                    tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_en)
                    changeSettingMsg = this.resources.getString(R.string.change_setting_msg_en)
                    setupToolbar()
                }
                "Japanese" -> {
                    appData_Singleton.getAppData()!!.appSettingData.languageApp = Constants.LANGUAGE_JP
                    tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_jp)
                    tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_jp)
                    changeSettingMsg = this.resources.getString(R.string.change_setting_msg_jp)
                    setupToolbar()
                }
            }
            var json = gson.toJson(appData_Singleton.getAppData()!!.appSettingData)
            prefs!!.appSettingData = json
            Utils.createToast(this, changeSettingMsg)
        })
    }

    private fun initalVar(){
        appData_Singleton = AppData_Singleton.getInstance()
        prefs = Prefs(this)
        when(appData_Singleton.getAppData()!!.appSettingData.translatedApi){
            Constants.YANDEX_API -> {
                set_rd_yd.isChecked = true
            }
            Constants.GOOGLE_API -> {
                set_rd_gg.isChecked = true
            }
        }

        when(appData_Singleton.getAppData()!!.appSettingData.languageApp){
            Constants.LANGUAGE_VN -> {
                set_rd_vn.isChecked = true
                tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_vn)
                tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_vn)
                changeSettingMsg = this.resources.getString(R.string.change_setting_msg_vn)
            }
            Constants.LANGUAGE_EN -> {
                set_rd_el.isChecked = true
                tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_en)
                tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_en)
                changeSettingMsg = this.resources.getString(R.string.change_setting_msg_en)
            }
            Constants.LANGUAGE_JP -> {
                set_rd_jp.isChecked = true
                tv_title_u_api.text = this.resources.getString(R.string.setting_api_title_jp)
                tv_title_u_language.text = this.resources.getString(R.string.setting_language_title_jp)
                changeSettingMsg = this.resources.getString(R.string.change_setting_msg_jp)
            }
        }
    }
}
