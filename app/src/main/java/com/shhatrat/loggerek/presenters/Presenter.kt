package com.shhatrat.loggerek.presenters

/**
 * Created by szymon on 7/11/17.
 */
interface Presenter<T : View> {
    var view: T?

    fun onDestory(){
        view = null
    }
}