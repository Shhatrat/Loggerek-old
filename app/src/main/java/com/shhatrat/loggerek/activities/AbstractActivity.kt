package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kenny.snackbar.SnackBarItem
import com.kenny.snackbar.SnackBarListener
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.*
import de.mateware.snacky.Snacky
import io.realm.Realm
import retrofit2.Response

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
        return null
    }

    fun getOP(v : String) : String {
        val o = v.lastIndexOf("OP")
        return v.substring(o, v.length)
    }

    fun success(request : LogRequest , u : Response<Log>) {
        if(!u.isSuccessful) {
            saveLogtoDb(request, u.message(), u.body()!!.message)
            showSnackbar(u)
            return
        }

        if(u.body()!!.message == OcResponse.SUCESS.message){
            showSnackbar(u)
            return
        }

        if(u.body()!!.message == OcResponse.ALREADY_CUBMITTED.message)
        {
            showSnackbar(u)
            saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }

        if( u.body()!!.message == OcResponse.PASSWORD.message){
            SnackBarItem.Builder(this)
                    .setMessage(u.body()!!.message)
                    .setSnackBarMessageColorResource(R.color.md_black_1000)
                    .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                    .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                    .setDuration(1500)
                    .setSnackBarListener(object : SnackBarListener {
                        override fun onSnackBarStarted(`object`: Any?) {
                        }

                        override fun onSnackBarFinished(`object`: Any?, actionPressed: Boolean) {
                            val intent = Intent(this@AbstractActivity, FullLogActivity::class.java)
                            intent.putExtra("unsend", request)
                            startActivity(intent)
                            finish()
                        }
                    })
                    .show()
            saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }
    }

    private fun showSnackbar(u: Response<Log>) {
        SnackBarItem.Builder(this)
                .setMessage(u.body()!!.message)
                .setSnackBarMessageColorResource(R.color.md_black_1000)
                .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                .setDuration(1500)
                .setSnackBarListener(object : SnackBarListener {
                    override fun onSnackBarStarted(`object`: Any?) {
                    }

                    override fun onSnackBarFinished(`object`: Any?, actionPressed: Boolean) {
                        finish()
                    }
                })
                .show()
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