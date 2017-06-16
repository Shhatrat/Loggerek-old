package com.shhatrat.loggerek

import android.app.getKoin
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.shhatrat.loggerek.api.Api


class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent)
        checkConfiguration()

        val ret by lazy { getKoin().get<Api>() }


        val intent = intent
        if (intent != null) {
            val uri = intent.data
            if (uri != null) {
                    Log.d("ddddd" , uri.toString() )
            }
        }

    }

    private fun checkConfiguration() {
        if (Data.consumerkey == null) {
            Toast.makeText(this, "No configured user", 1000).show()
            startActivity(Intent(this, ConfigActivity::class.java))
        }
    }
}
