package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Data
import com.shhatrat.loggerek.models.LogRequest
import io.realm.Realm

abstract  class AbstractActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkConfiguration()
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
        if(intent.getParcelableExtra<LogRequest>("unsend") != null) {
            val parcel = intent.getParcelableExtra<LogRequest>("unsend")
            return parcel.cache_code
        }
        return null
    }

    fun getOP(v : String) : String {
        val o = v.lastIndexOf("OP")
        return v.substring(o, v.length)
    }

    val sharedPreferences by lazy{getKoin().get<SharedPreferences>()}
    val realm by lazy{getKoin().get<Realm>()}
}