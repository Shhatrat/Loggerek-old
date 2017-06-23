package com.shhatrat.loggerek.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shhatrat.loggerek.models.Data

/**
 * Created by szymon on 17.06.17.
 */
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
            Toast.makeText(this, "No configured user", 1000).show()
            startActivity(Intent(this, ConfigActivity::class.java))
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