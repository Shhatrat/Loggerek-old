package com.shhatrat.loggerek

import com.chibatching.kotpref.KotprefModel

/**
 * Created by szymon on 16.06.17.
 */
object Data : KotprefModel(){
    var userName by nullableStringPref()
    var age by intPref(default = 14)
    var consumerkey by nullableStringPref()
    var consumerSecret by nullableStringPref()
}