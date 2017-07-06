package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.*
import de.mateware.snacky.Snacky
import io.realm.Realm
import retrofit2.Response

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

    fun success(request : LogRequest , u : Response<Log>) {
        if(!u.isSuccessful) {
            saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }

        if(u.message() == OcResponse.SUCESS.message){
            val sn = Snacky.builder().setActivty(this).setText(u.body()!!.message).setDuration(2000)
            val callback = sn.build()
            callback.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    finish()
                }
            })
            sn.success().show()
            return
        }

        if(u.body()!!.message == OcResponse.ALREADY_CUBMITTED.message)
        {
            val sn = Snacky.builder().setActivty(this).setText(u.body()!!.message).setDuration(2000)
            val callback = sn.build()
            callback.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    finish()
                }
            })
            sn.error().show()
            saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }

        if( u.body()!!.message == OcResponse.PASSWORD.message){
            val sn = Snacky.builder().setActivty(this).setText(u.body()!!.message).setDuration(2000)
            val callback = sn.build()
            callback.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    startActivity(Intent(this@AbstractActivity, FullLogActivity::class.java))
                    //add bundle
                }
            })
            saveLogtoDb(request, u.message(), u.body()!!.message)
        }
    }

    fun error(request : LogRequest,  u: Throwable) {
        val sn = Snacky.builder().setActivty(this).setText(u.message).setDuration(2000).error()
        sn.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                finish()
            }
        })
        sn.show()
        saveLogtoDb(request, u.message!! , OcResponse.NO_INTERNET.message)
    }

    private fun saveLogtoDb(request: LogRequest, errorMessage : String , type : String) {

        if(sharedPreferences.getBoolean("quick_save", true))
        {
            val errorLog = Unsend()
            errorLog.cacheOp = request.cache_code
            errorLog.errorMessage  = errorMessage
            errorLog.log = request.comment
            errorLog.type = type
            errorLog.timestamp = System.currentTimeMillis()

            errorLog.logDate = request.logDate
            errorLog.logtype = request.logtype
            errorLog.rating = request.rating
            errorLog.password = request.password
            errorLog.recommend = request.recommend
            realm.beginTransaction()
            realm.insert(errorLog)
            realm.commitTransaction()
        }
    }

    val sharedPreferences by lazy{getKoin().get<SharedPreferences>()}
    val realm by lazy{getKoin().get<Realm>()}
}