package com.shhatrat.loggerek.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Data

abstract  class AbstractActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkConfiguration()
        checkIntent()
    }

    fun AppCompatActivity.checkIntent() {
        val intent = intent
        if (intent != null) {
            val uri = intent.data
            if (uri == null) {
                finish()
            }
        }else finish()
    }

    fun checkConfiguration() {
        if (Data.consumerkey == null) {
            Toast.makeText(this, getString(R.string.no_configured_user), 1000).show()
            startActivity(Intent(this, ConfigActivity::class.java))
            finish()
        }
    }

    fun getOpFormIntent() : String?{
        if (intent != null) {
            val uri = intent.data
            if (uri != null) {
                return getOP(uri.toString())
            }
        }
        return null
    }

    fun getOP(v : String) : String {
        val o = v.lastIndexOf("OP")
        return v.substring(o, v.length)
    }
}