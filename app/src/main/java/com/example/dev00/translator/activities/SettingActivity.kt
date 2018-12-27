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
import com.example.dev00.translator.utils.Utils
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity() {

    private lateinit var appData_Singleton: AppData_Singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupToolbar()
        initalVar()
        initalListener()
    }

    private fun setupToolbar(){
        setSupportActionBar(setting_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = Constants.TITLE_SETTING

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
                    appData_Singleton.getAppData()!!.api = Constants.GOOGLE_API
                     Utils.createToast(this, "Changed successfully.")
                }
                "Yandex API" -> {
                    appData_Singleton.getAppData()!!.api = Constants.YANDEX_API
                    Utils.createToast(this, "Changed successfully.")
                }
            }
        })
    }

    private fun initalVar(){
        appData_Singleton = AppData_Singleton.getInstance()
        when(appData_Singleton.getAppData()!!.api){
            Constants.YANDEX_API -> {
                set_rd_yd.isChecked = true
            }
            Constants.GOOGLE_API -> {
                set_rd_gg.isChecked = true
            }
        }
    }

}
