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
        addSlide(AppIntro2Fragment.newInstance(getString(R.string.t1), getString(R.string.d1), R.drawable.i1 , ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t2), getString(R.string.d2), R.drawable.i2 , ContextCompat.getColor(this, R.color.material)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t3), getString(R.string.d3), R.drawable.i3 , ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t4), getString(R.string.d4) , R.drawable.i4, ContextCompat.getColor(this, R.color.material3)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t5), getString(R.string.d5) , R.drawable.i5, ContextCompat.getColor(this, R.color.material)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t6), getString(R.string.d6) , R.drawable.i6, ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t7), getString(R.string.d7) , R.drawable.i7, ContextCompat.getColor(this, R.color.material)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t8), getString(R.string.d8) , R.drawable.i8, ContextCompat.getColor(this, R.color.material)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t9), "" , R.drawable.i9, ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t10), getString(R.string.d101) , R.drawable.i10, ContextCompat.getColor(this, R.color.material3)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t101) ,getString(R.string.d10) , R.drawable.i9, ContextCompat.getColor(this, R.color.material2)))
        addSlide(AppIntroFragment.newInstance(getString(R.string.t11) ,getString(R.string.d11) , R.drawable.i11, ContextCompat.getColor(this, R.color.material)))
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
