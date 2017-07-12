package com.shhatrat.loggerek.api

import com.shhatrat.loggerek.models.OcResponse.*
import android.app.Activity
import android.app.getKoin
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.Snackbar
import com.kenny.snackbar.SnackBarItem
import com.kenny.snackbar.SnackBarListener
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.activities.FullLogActivity
import com.shhatrat.loggerek.models.*
import de.mateware.snacky.Snacky
import io.realm.Realm
import retrofit2.Response

/**
 * Created by szymon on 06.07.17.
 */
class LogHandler(val activity : Activity) {


    val sharedPreferences by lazy{activity.getKoin().get<SharedPreferences>()}
    val realm by lazy{activity.getKoin().get<Realm>()}
    val retrofit by lazy{activity.getKoin().get<Api>()}


    fun success(request : LogRequest, u : Response<Log>, note : String? = null, finishAfter :Boolean = false, saveLog : Boolean = true) {
        if(!u.isSuccessful) {
            if(saveLog)
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            showSnackbar(u, finishAfter)
            return
        }

        if(u.body()!!.message == OcResponse.SUCESS.message){
            showSnackbar(u, finishAfter)

            if(!note.isNullOrEmpty()) {
                retrofit.saveNote(request.cache_code!!, "Hasło = $note")
                        .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe({
                            //                    u -> setupData(u)
                        }, {
                            //                    e -> setupOfflineData(getOpFormIntent())
                        })
            }

            removeLogsFromDB(request)
            return
        }

        if(u.body()!!.message == OcResponse.ALREADY_CUBMITTED.message)
        {
            showSnackbar(u, finishAfter)
            if(saveLog)
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }

        if( u.body()!!.message == OcResponse.REQ_PASSWORD.message){
            SnackBarItem.Builder(activity)
                    .setMessage(u.body()!!.message)
                    .setSnackBarMessageColorResource(R.color.md_black_1000)
                    .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                    .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                    .setDuration(1500)
                    .setSnackBarListener(object : SnackBarListener {
                        override fun onSnackBarStarted(`object`: Any?) {
                        }

                        override fun onSnackBarFinished(`object`: Any?, actionPressed: Boolean) {
                            val intent = Intent(activity, FullLogActivity::class.java)
                            intent.putExtra("unsend", request)
                            activity.startActivity(intent)
                            if(finishAfter)
                            activity.finish()
                        }
                    })
                    .show()
            if(saveLog)
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }
    }


    private fun showSnackbar(u: Response<Log>, finishAfter: Boolean = true) {
        SnackBarItem.Builder(activity)
                .setMessage(u.body()!!.message.translateResponse(activity))
                .setSnackBarMessageColorResource(R.color.md_black_1000)
                .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                .setDuration(1500)
                .setSnackBarListener(object : SnackBarListener {
                    override fun onSnackBarStarted(`object`: Any?) {
                    }

                    override fun onSnackBarFinished(`object`: Any?, actionPressed: Boolean) {
                        if(finishAfter)
                        activity.finish()
                    }
                })
                .show()
    }

    fun error(request : LogRequest,  u: Throwable) {
        val sn = Snacky.builder().setActivty(activity).setText(u.message).setDuration(2000).error()
        sn.addCallback(object : Snackbar.Callback(){
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                activity.finish()
            }
        })
        sn.show()
        realm.saveLogtoDb(request, u.message!! , OcResponse.NO_INTERNET.message)
    }

    private fun removeLogsFromDB(request: LogRequest) {
        realm.beginTransaction()
        realm.where(Unsend::class.java)
                .equalTo("logtype", request.logtype)
                .equalTo("cacheOp", request.cache_code)
                .findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    fun Realm.saveLogtoDb(request: LogRequest, errorMessage : String , type : String) {

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
            this.beginTransaction()
            this.insert(errorLog)
            this.commitTransaction()
        }
}
}
