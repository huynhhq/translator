package com.example.dev00.translator.activities

import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatDrawableManager
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.Constants
import com.example.dev00.translator.models.AppData_Singleton
import kotlinx.android.synthetic.main.activity_about_us.*

class AboutUsActivity : AppCompatActivity() {

    private lateinit var appData_Singleton: AppData_Singleton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        appData_Singleton = AppData_Singleton.getInstance()
        setupToolbar()
        initalView()
    }

    private fun setupToolbar(){
        setSupportActionBar(about_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar!!

        // Set toolbar title/app title
        when(appData_Singleton.getAppData()!!.appSettingData.languageApp){
            Constants.LANGUAGE_JP -> {
                actionBar.title = this.resources.getString(R.string.toolbar_about_us_jp)
            }
            Constants.LANGUAGE_VN -> {
                actionBar.title = this.resources.getString(R.string.toolbar_about_us_vn)
            }
            Constants.LANGUAGE_EN -> {
                actionBar.title = this.resources.getString(R.string.toolbar_about_us_en)
            }
        }
        // Display the app icon in action bar/toolbar
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //change color of back arrow, use AppCompatDrawableManager for pre-Lollipop version
        val backArrow = AppCompatDrawableManager.get().getDrawable(this ,R.drawable.ic_arrow_back_black_24dp)
        backArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        about_toolbar.setNavigationOnClickListener({
            onBackPressed()
        })
    }

    private fun initalView(){
        when(appData_Singleton.getAppData()!!.appSettingData.languageApp){
            Constants.LANGUAGE_EN -> {
                tv_about_title.text = this.resources.getString(R.string.about_en)
                tv_name.text = this.resources.getString(R.string.name_en)
                tv_ho.text = this.resources.getString(R.string.head_office_en)
                tv_director.text = this.resources.getString(R.string.director_en)
                tv_capital.text = this.resources.getString(R.string.capital_en)
                tv_mp.text = this.resources.getString(R.string.main_partner_en)
                tv_mr.text = this.resources.getString(R.string.mr_en)
                tv_address.text = this.resources.getString(R.string.address_en)
                tv_invest.text = this.resources.getString(R.string.invest_en)
            }
            Constants.LANGUAGE_VN -> {
                tv_about_title.text = this.resources.getString(R.string.about_vn)
                tv_name.text = this.resources.getString(R.string.name_vn)
                tv_ho.text = this.resources.getString(R.string.head_office_vn)
                tv_director.text = this.resources.getString(R.string.director_vn)
                tv_capital.text = this.resources.getString(R.string.capital_vn)
                tv_mp.text = this.resources.getString(R.string.main_partner_vn)
                tv_mr.text = this.resources.getString(R.string.mr_vn)
                tv_address.text = this.resources.getString(R.string.address_vn)
                tv_invest.text = this.resources.getString(R.string.invest_vn)
            }
            Constants.LANGUAGE_JP -> {
                tv_about_title.text = this.resources.getString(R.string.about_jp)
                tv_name.text = this.resources.getString(R.string.name_jp)
                tv_ho.text = this.resources.getString(R.string.head_office_jp)
                tv_director.text = this.resources.getString(R.string.director_jp)
                tv_capital.text = this.resources.getString(R.string.capital_jp)
                tv_mp.text = this.resources.getString(R.string.main_partner_jp)
                tv_mr.text = this.resources.getString(R.string.mr_jp)
                tv_address.text = this.resources.getString(R.string.address_jp)
                tv_invest.text = this.resources.getString(R.string.invest_jp)
            }
        }
    }
}
