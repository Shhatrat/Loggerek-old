package com.shhatrat.loggerek.models

import io.realm.RealmObject

/**
 * Created by szymon on 6/27/17.
 */
open class Unsend : RealmObject(){
    var cacheOp : String? =  ""
    var log : String? = ""
    var errorMessage : String? = ""
    var type : String? = ""
    var timestamp : Long? = 0

    var logtype : String? =null
    var logDate : String? =null
    var recommend : Boolean? =null
    var rating : Int? =null
    var password : String? =null
}