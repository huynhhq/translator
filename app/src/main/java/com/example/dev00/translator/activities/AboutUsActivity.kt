package com.example.dev00.translator.activities

import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatDrawableManager
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.Constants
import kotlinx.android.synthetic.main.activity_about_us.*
import kotlinx.android.synthetic.main.activity_setting.*

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        setupToolbar()
    }

    private fun setupToolbar(){
        setSupportActionBar(about_toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = Constants.TITLE_ABOUT

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
}
