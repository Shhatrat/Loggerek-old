package com.shhatrat.loggerek.activities

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.api.LogHandler
import com.shhatrat.loggerek.di.StupidSingleton
import com.shhatrat.loggerek.fragments.LogFragment
import com.shhatrat.loggerek.models.LogRequest
import com.shhatrat.loggerek.models.SingleLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_log.*
import java.util.*


class LogActivity : AbstractActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        window.decorView.setBackgroundResource(android.R.color.transparent)

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
        val ret by lazy { StupidSingleton.ocApi(this) }
        hideFabsShowProgress()
        val logRequest = LogRequest(getOP(fullLink), "Found it", log)
        ret.logEntry(getOP(fullLink), "Found it", log)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> run{
                    f_progress.visibility = View.GONE
                    LogHandler(this).success(logRequest, u)
                }}, {
                    e ->
                    run {
                        f_progress.visibility = View.GONE
                        LogHandler(this).error(logRequest, e) }
                })
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