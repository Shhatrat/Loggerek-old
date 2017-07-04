package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View.GONE
import android.view.View.VISIBLE
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.fragments.LogFragment
import com.shhatrat.loggerek.models.OcResponse
import com.shhatrat.loggerek.models.SingleLog
import com.shhatrat.loggerek.models.Unsend
import de.mateware.snacky.Snacky
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_log.*
import retrofit2.Response
import java.util.*


class LogActivity : AbstractActivity() {

    val sharedPreferences by lazy{getKoin().get<SharedPreferences>()}
    val realm by lazy{getKoin().get<Realm>()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        window.decorView.setBackgroundResource(android.R.color.transparent)

        checkConfiguration()
        checkIntent()

        val intent = intent
        val op = getOP(intent.data.toString())

        f_mylog.setOnClickListener { logCache(op, log(LogFragment.Type.DEFAULT)) }
        f_good.setOnClickListener { logCache(op, log(LogFragment.Type.GOOD)) }
        f_bad.setOnClickListener { logCache(op, log(LogFragment.Type.BAD)) }
    }

    private fun  log(type: LogFragment.Type): String {
        var list = realm.getLog(LogFragment.Type.GOOD)
        if(list.isEmpty()) {
            list = arrayListOf(getString(R.string.thanks_for_cache))
        }
        when(type){
            LogFragment.Type.GOOD -> {
                if(sharedPreferences.getBoolean("mix_good", false))
                    return mixLogs(list)
                if(sharedPreferences.getBoolean("random_good", false))
                    return randomLogs(list)
            }
            LogFragment.Type.BAD -> {
                if(sharedPreferences.getBoolean("mix_bad", false))
                    return mixLogs(list)
                if(sharedPreferences.getBoolean("random_bad", false))
                    return randomLogs(list)
            }
            LogFragment.Type.DEFAULT -> {
                if(sharedPreferences.getBoolean("mix_default", false))
                   return mixLogs(list)
                if(sharedPreferences.getBoolean("random_default", false))
                   return randomLogs(list)
            }
        }
        return getString(R.string.thanks_for_cache)
    }

    private fun  randomLogs(list: List<String?>): String {
        val r =  Math.random() * list.size
        return list[r.toInt()]!!
    }

    private fun  mixLogs(list: List<String?>): String {
        val random = Random().nextInt(list.size)
        val c = hashMapOf<Int , String>()
        for(i in random downTo 0)
        {
           val index =  Random().nextInt(list.size)
           c.put(index, list[index]!!)
        }
        var output = ""
        c.map { u -> u.value }
                .toList()
                .forEach { u -> output= "$output $u" }
        return output
    }


    private fun logCache(fullLink: String, log : String){
        val ret by lazy { getKoin().get<Api>() }
        hideFabsShowProgress()
        ret.logEntry(getOP(fullLink), "Found it", log)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> success(getOP(fullLink), log, u)}, {
                    e -> error(getOP(fullLink), log, e)})
    }

    fun success(cacheOp : String, log: String, u : Response<com.shhatrat.loggerek.models.Log> ) {
        if(!u.isSuccessful) {
            saveLogtoDb(cacheOp, log, u.message(), u.body()!!.message)
            return
        }

        if(u.message() == OcResponse.SUCESS.message){
            val sn = Snacky.builder().setActivty(this).setText(u.body()!!.message).setDuration(2000)
            val callback = sn.build()
            f_progress.visibility = GONE
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
            f_progress.visibility = GONE
            callback.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    finish()
                }
            })
            sn.error().show()
            saveLogtoDb(cacheOp, log, u.message(), u.body()!!.message)
            return
        }

        if( u.body()!!.message == OcResponse.PASSWORD.message){
            val sn = Snacky.builder().setActivty(this).setText(u.body()!!.message).setDuration(2000)
            val callback = sn.build()
            f_progress.visibility = GONE
            callback.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    startActivity(Intent(this@LogActivity, FullLogActivity::class.java))
                    //add bundle
                }
            })
            saveLogtoDb(cacheOp, log, u.message(), u.body()!!.message)
        }
    }

    private fun saveLogtoDb(cacheOp: String, log: String, u : String , type : String) {

        if(sharedPreferences.getBoolean("quick_save", true))
        {
            val errorLog = Unsend()
            errorLog.cacheOp = cacheOp
            errorLog.errorMessage  = u
            errorLog.log = log
            errorLog.type = type
            errorLog.timestamp = System.currentTimeMillis()
            realm.beginTransaction()
            realm.insert(errorLog)
            realm.commitTransaction()
        }
    }

    fun error(cacheOp : String, log: String, u: Throwable) {
        f_progress.visibility = GONE
        val sn = Snacky.builder().setActivty(this).setText(u.message).setDuration(2000).error()
        sn.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                finish()
            }
        })
        sn.show()
        saveLogtoDb(cacheOp, log, u.message!! , OcResponse.NO_INTERNET.message)
    }

    private fun hideFabsShowProgress(){
        f_mylog.visibility = GONE
        f_bad.visibility = GONE
        f_good.visibility = GONE
        f_progress.visibility= VISIBLE
    }

    fun Realm.getLog(type : LogFragment.Type) : List<String?>{
        val l = this.where(SingleLog::class.java)
                .equalTo("type", type.name)
                .findAll().toList().map { log -> log.log }
        return ArrayList(l)
    }
}