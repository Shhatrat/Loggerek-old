package com.shhatrat.loggerek.presenters

import com.shhatrat.loggerek.models.LogRequest

/**
 * Created by szymon on 7/11/17.
 */
interface FullLogPresenter<T : View> : Presenter<T> {
    fun getDataFromIntent(op : String)
    fun getDataFromDb(log : LogRequest)
    fun changeLogType(logType: String?)
    fun send(logRequest: LogRequest, passToNote : Boolean, note : String?)
}