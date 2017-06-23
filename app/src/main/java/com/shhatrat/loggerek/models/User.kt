package com.shhatrat.loggerek.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by szymon on 16.06.17.
 */
open class User : RealmObject(){
    @PrimaryKey
    var username : String? = ""
    var profile_url : String? = ""
    var home_location : String? = ""
    var caches_found : Int? = 0
    var caches_notfound : Int? = 0
    var caches_hidden : Int? = 0
}