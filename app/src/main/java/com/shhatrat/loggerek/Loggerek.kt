package com.shhatrat.loggerek

import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import com.shhatrat.loggerek.di.Module
import org.koin.android.KoinApplication

/**
 * Created by szymon on 16.06.17.
 */
class Loggerek : KoinApplication(Module::class){

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        Kotpref.gson = Gson()
    }
}