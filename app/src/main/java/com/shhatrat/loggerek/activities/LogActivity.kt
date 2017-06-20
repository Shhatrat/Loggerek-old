package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.shhatrat.loggerek.models.Data
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import de.mateware.snacky.Snacky
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_log.*


class LogActivity : AbstractActivity() {

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

        f_mylog.setOnClickListener { logCache(op, getDefaultLog()) }
        f_good.setOnClickListener { logCache(op, getGoodLog()) }
        f_bad.setOnClickListener { logCache(op, getBadLog()) }
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
        var sn = Snacky.builder().setActivty(this).setText(u.message).setDuration(2000)
        if(u.success)
            sn.success()
        else
            sn.error()
        var oo = sn.build()
        f_progress.visibility = GONE
        oo.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                finish()
            }
        })
        oo.show()

    }

    fun error(message: String?) {
        f_progress.visibility = GONE
        val sn = Snacky.builder().setActivty(this).setText(message).setDuration(2000).error()
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
        if(Data.defaultLog !=null)
        if(Data.defaultLog!!.isEmpty())
            return "Dzięki za skrzynkę!"
        return Data.defaultLog ?: "Dzięki za skrzynkę!"
    }

    private fun getBadLog() : String{
        if(Data.badLog !=null)
            if(Data.badLog!!.isEmpty())
                return "Dzięki za skrzynkę!"
        return Data.badLog ?: "Dzięki za skrzynkę!"
    }

    private fun getGoodLog() : String{
        if(Data.goodLog !=null)
            if(Data.goodLog!!.isEmpty())
                return "Dzięki za skrzynkę!"
        return Data.goodLog ?: "Dzięki za skrzynkę!"
    }

    private fun getOP(v : String) : String {
        val o = v.lastIndexOf("OP")
        return v.substring(o, v.length)
    }
}
