package com.shhatrat.loggerek

import com.chibatching.kotpref.KotprefModel

/**
 * Created by szymon on 16.06.17.
 */
object Data : KotprefModel(){
    var userName by nullableStringPref()
    var consumerkey by nullableStringPref()
    var consumerSecret by nullableStringPref()
    var defaultLog by nullableStringPref()
}