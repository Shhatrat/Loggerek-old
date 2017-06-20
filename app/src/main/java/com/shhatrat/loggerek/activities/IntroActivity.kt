package com.shhatrat.loggerek.activities

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntro2Fragment
import com.github.paolorotolo.appintro.AppIntroFragment
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Data

class IntroActivity : AppIntro2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        //todo finish
        addSlide(AppIntro2Fragment.newInstance(title, "ddd", R.drawable.ic_arrow_back_white , ContextCompat.getColor(this, R.color.material0)))
        addSlide(AppIntroFragment.newInstance(title, "ddd", R.drawable.ic_arrow_back_white , ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(title, "ddd", R.drawable.ic_arrow_back_white , ContextCompat.getColor(this, R.color.material)))
        addSlide(AppIntroFragment.newInstance("", "" , R.drawable.ic_arrow_back_white, ContextCompat.getColor(this, R.color.material3)))
        addSlide(o)


        showSkipButton(false)
        isProgressButtonEnabled = false
        showStatusBar(false)
        Data.introViewed = true
    }

    lateinit var o : Fragment

    fun init(){
       o =  AppIntro2Fragment.newInstance("", "", R.drawable.md_transparent , ContextCompat.getColor(this, R.color.material3))
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        if(o == newFragment)
            finish()
    }
}
