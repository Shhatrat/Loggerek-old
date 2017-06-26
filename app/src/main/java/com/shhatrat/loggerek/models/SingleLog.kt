package com.shhatrat.loggerek.models

import com.shhatrat.loggerek.fragments.LogFragment
import io.realm.RealmObject

/**
 * Created by szymon on 6/26/17.
 */
open class SingleLog : RealmObject() {

    var log : String? = ""
    var type : String? = ""

    fun saveEnum(i : LogFragment.Type) {
        type = i.name
    }

    fun loadEnum() : LogFragment.Type{
        return LogFragment.Type.valueOf(type!!)
    }
}