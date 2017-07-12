package com.shhatrat.loggerek.presenters

import android.app.Activity
import com.kenny.snackbar.SnackBarItem
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.activities.getUTF8String
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.api.LogHandler
import com.shhatrat.loggerek.di.StupidSingleton
import com.shhatrat.loggerek.models.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import retrofit2.Response

/**
 * Created by szymon on 7/11/17.
 */
class FullLogPresenterImpl(override var view: FullLogView?, val retrofit : Api): FullLogPresenter<FullLogView> {


    var event : Boolean? = null

    override fun changeLogType(logType: String?) {
        event?.let {
            val isEvent = event
            if (logType == activity.getString(R.string.found_it) && !isEvent!!) {
                view?.changePasswordVisibility(true)
                view?.changeSavePasswordVisibility(true)
                view?.changeRatesVisibility(true)
                view?.changeRecoVisibility(true)
            } else {
                view?.changePasswordVisibility(false)
                view?.changeSavePasswordVisibility(false)
                view?.changeRatesVisibility(false)
                view?.changeRecoVisibility(false)
            }
            if (logType == activity.getString(R.string.attended) && isEvent!!)
                view?.changeRatesVisibility(true)
        }
    }

    override fun send(logRequest: LogRequest, passToNote: Boolean, note : String?) {
        view?.startLoading()
        retrofit.logEntryFull(logRequest.cache_code!!,
                logRequest.logtype!!,
                logRequest.comment?:"",
                logRequest.logDate!!,
                logRequest.recommend?:false,
                logRequest.rating,
                logRequest.password!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> run {
                            view?.stopLoading()
                            success(logRequest, u, note )}},
                        {
                    e ->
                    run {
                        view?.stopLoading()
                        LogHandler(activity).error(logRequest, e)
                    }
                })

            if(!note.isNullOrEmpty()) {
                retrofit.saveNote(logRequest.cache_code, note!!)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //                    u -> setupData(u)
                    }, {
                        //                    e -> setupOfflineData(getOpFormIntent())
                    })
            }
    }

    override fun getDataFromIntent(op: String) {
        downloadCache(op)
    }

    lateinit var activity : Activity
    init {
        activity = view as Activity
    }

    override fun getDataFromDb(log: LogRequest) {
        downloadCache(log.cache_code!!)
        preapreFromLogRequest(log)
    }

    private fun  preapreFromLogRequest(log: LogRequest) {
        view?.changeLog(log.comment)
        view?.changeReco(log.recommend)
        view?.changeRates(log.rating)
        view?.changePassword(log.password)
        view?.changeLogSpinnerSelected(log.logtype)
    }

    fun downloadCache(cacheOp : String){
        setupOfflineData(cacheOp, false)
            retrofit.geocache(cacheOp, "name|location|type|recommendations|founds|req_passwd".getUTF8String())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> setupData(u)
                }, {
                    e -> setupOfflineData(cacheOp, true)
                })
            }

    private fun setupOfflineData(opFormIntent: String, notification : Boolean) {
        if(notification) view?.showNotification("Offline mode", null, false)
        view?.changeOnClickTitleListener("https://opencaching.pl/$opFormIntent")
        view?.changeTitle(opFormIntent)
        view?.changePasswordVisibility(true)
        view?.changeRatesVisibility(true)
        view?.changeRecoVisibility(true)
        view?.changeSavePasswordVisibility(true)
        view?.changeLogSpinnerItems(listOf(
                activity.getString(R.string.found_it),
                activity.getString(R.string.will_attend),
                activity.getString(R.string.attended),
                activity.getString(R.string.comment),
                activity.getString(R.string.didnt_fint_it)))
    }

    fun setupData(u: Cache){
        event = u.type =="event"
        view?.changeTitle(u.name)
        if(u.type == "Event"){
            view?.changeRecoVisibility(false)
            view?.changeSavePasswordVisibility(false)
            view?.changePasswordVisibility(false)
            view?.changeLogSpinnerItems(listOf(
                    activity.getString(R.string.will_attend),
                    activity.getString(R.string.attended),
                    activity.getString(R.string.comment)))
        }

        if(u.type != "Event"){
            view?.changeRecoVisibility(true)
            view?.changeSavePasswordVisibility(true)
            view?.changePasswordVisibility(true)
            view?.changeLogSpinnerItems(listOf(
                    activity.getString(R.string.found_it),
                    activity.getString(R.string.comment),
                    activity.getString(R.string.didnt_fint_it)))
        }
        view?.changeIconCacheType(u.type)
        view?.changeMap(preapreGoogleMapsLink(u.location))
        view?.changePasswordVisibility(u.req_passwd)
        view?.changeSavePasswordVisibility(u.req_passwd)
    }


    private fun  preapreGoogleMapsLink(home_location: String?): String{
        val first = "https://maps.googleapis.com/maps/api/staticmap?center="
        val second = "&markers=color:red%7Clabel:%7C${home_location!!.replace("|", ",")}&zoom=14&size=500x200&maptype=roadmap&key="
        val key =  activity.getString(R.string.google_maps_key)
        if(!home_location.isNullOrBlank())
            return "$first${home_location!!.replace("|", ",")}$second$key"
        else
            return "${first}51.743792, 19.450380&zoom=6&size=600x300&maptype=roadmap&key=$key"
    }



    fun success(request : LogRequest, u : Response<Log>, note : String? = null) {
        view?.normalPassword()
        val realm by lazy{StupidSingleton.realm()}
        if(!u.isSuccessful) {
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            view?.showNotification(u.body()!!.message, null, false)
            return
        }

        if(u.body()!!.message == OcResponse.SUCESS.message){
            view?.showNotification(u.body()!!.message, null, true)

            if(!note.isNullOrEmpty()) {
                retrofit.saveNote(request.cache_code!!, "Hasło = $note")
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            //                    u -> setupData(u)
                        }, {
                            //                    e -> setupOfflineData(getOpFormIntent())
                        })
            }
            realm.removeLogsFromDB(request)
            return
        }

        if(u.body()!!.message == OcResponse.ALREADY_CUBMITTED.message)
        {
            view?.showNotification(u.body()!!.message, null, true)
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }
        if( u.body()!!.message == OcResponse.BAD_PASSWORD.message){
            SnackBarItem.Builder(activity)
                    .setMessage(u.body()!!.message)
                    .setSnackBarMessageColorResource(R.color.md_black_1000)
                    .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                    .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                    .setDuration(1500)
                    .show()
            view?.redPassword()
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
        }
        if( u.body()!!.message == OcResponse.REQ_PASSWORD.message){
            SnackBarItem.Builder(activity)
                    .setMessage(u.body()!!.message)
                    .setSnackBarMessageColorResource(R.color.md_black_1000)
                    .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                    .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                    .setDuration(1500)
                    .show()
            view?.redPassword()
            realm.saveLogtoDb(request, u.message(), u.body()!!.message)
            return
        }
    }
    fun Realm.saveLogtoDb(request: LogRequest, errorMessage : String , type : String) {
        val sharedPreferences by lazy { StupidSingleton.sharedPreferences(activity) }

        if (sharedPreferences.getBoolean("quick_save", true)) {
            val errorLog = Unsend()
            errorLog.cacheOp = request.cache_code
            errorLog.errorMessage = errorMessage
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

    private fun Realm.removeLogsFromDB(request: LogRequest) {
        this.beginTransaction()
        this.where(Unsend::class.java)
                .equalTo("logtype", request.logtype)
                .equalTo("cacheOp", request.cache_code)
                .findAll().deleteAllFromRealm()
        this.commitTransaction()
    }