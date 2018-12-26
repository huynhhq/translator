package com.example.dev00.translator.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dev00.translator.R
import com.example.dev00.translator.helpers.Constants
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat


class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        setupToolbar()
    }

    private fun setupToolbar(){
        setSupportActionBar(toolbar)
        // Now get the support action bar
        val actionBar = supportActionBar

        // Set toolbar title/app title
        actionBar!!.title = Constants.TITLE_SETTING

        // Display the app icon in action bar/toolbar
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        //change color of back arrow
        val backArrow = ContextCompat.getDrawable(this ,R.drawable.ic_arrow_back_black_24dp)
        backArrow!!.setColorFilter(ContextCompat.getColor(this,R.color.md_white_1000), PorterDuff.Mode.SRC_ATOP)
        supportActionBar!!.setHomeAsUpIndicator(backArrow)

        toolbar.setNavigationOnClickListener({
            onBackPressed()
        })
    }

}
