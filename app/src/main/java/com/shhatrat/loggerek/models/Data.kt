package com.shhatrat.loggerek.models

import com.chibatching.kotpref.KotprefModel

/**
 * Created by szymon on 16.06.17.
 */
object Data : KotprefModel(){
    var userName by nullableStringPref()
    var consumerkey by nullableStringPref()
    var consumerSecret by nullableStringPref()
    var defaultLog by nullableStringPref()
    var goodLog by nullableStringPref()
    var badLog by nullableStringPref()
    var introViewed by booleanPref(false)
}