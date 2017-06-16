package com.shhatrat.loggerek

import android.app.getKoin
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.shhatrat.loggerek.api.Api
import de.mateware.snacky.Snacky
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_log.*


class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent)



        checkConfiguration()
        checkIntent()

        val intent = intent
        if (intent != null) {
            val uri = intent.data
            if (uri != null) {
                    Log.d("ddddd" , uri.toString() )
            }
        }
        val op = getOP(intent.data.toString())

        f_mylog.setOnClickListener {
        logCache(op, getDefaultLog())
        }

    }


    private fun logCache(fullLink: String, log : String){
        val ret by lazy { getKoin().get<Api>() }
        hideFabsShowProgress()
        ret.logEntry(getOP(fullLink), "Found it", log)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> success(u.body()!!)}, {
                    e -> error(e.message)})
    }

    fun success(u: com.shhatrat.loggerek.models.Log) {
       val sn = Snacky.builder().setActivty(this).setText(u.message).info()
        f_progress.visibility = GONE
        sn.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                finish()
            }
        })
        sn.show()

    }

    fun error(message: String?) {
        f_progress.visibility = GONE
        val sn = Snacky.builder().setActivty(this).setText(message).error()
        sn.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                finish()
            }
        })
        sn.show()
    }

    private fun hideFabsShowProgress(){
        f_mylog.visibility = GONE
        f_bad.visibility = GONE
        f_good.visibility = GONE
        f_progress.visibility= VISIBLE
    }

    private fun getDefaultLog() : String{
        if(Data.defaultLog!=null)
        if(Data.defaultLog!!.isEmpty())
            return "Dzięki za skrzynkę!"
        return Data.defaultLog?: "Dzięki za skrzynkę!"
    }

    private fun getOP(v : String) : String {
        val o = v.lastIndexOf("OP")
        return v.substring(o, v.length)
    }

    private fun checkIntent() {
        val intent = intent
        if (intent != null) {
            val uri = intent.data
            if (uri == null) {
                finish()
            }
        }else finish()
    }

    private fun checkConfiguration() {
        if (Data.consumerkey == null) {
            Toast.makeText(this, "No configured user", 1000).show()
            startActivity(Intent(this, ConfigActivity::class.java))
        }
    }
}
