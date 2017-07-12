package com.shhatrat.loggerek.presenters

/**
 * Created by szymon on 7/11/17.
 */
interface FullLogView : View {

    fun changeTitle(title : String?)
    fun changeOnClickTitleListener(address : String?)
    fun changeMap(address : String?)
    fun changeIconCacheType(cacheType :String?)
    fun changeLog(log: String?)
    fun changeRatesVisibility(visible : Boolean?)
    fun changeRates(rate : Int?)
    fun changeReco(recommend : Boolean?)
    fun changeRecoText(text : String?)
    fun changeRecoVisibility(visible : Boolean?)
    fun changeLogSpinnerItems(list : List<String>?)
    fun changeLogSpinnerPosition(list : List<String>?, position :Int?)
    fun changeDate(date : String?)
    fun changeLogSpinnerSelected(item : String?)
    fun changePassword(password : String?)
    fun changePasswordVisibility(visible : Boolean)
    fun changeSavePassword(save : Boolean?)
    fun changeSavePasswordVisibility(visible : Boolean)
    fun showNotification(text : String?, type : String?, finishAfterSeen : Boolean)
    fun redPassword()
    fun normalPassword()

    fun startLoading()
    fun stopLoading()
}