package com.shhatrat.loggerek

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm

/**
 * Created by szymon on 16.06.17.
 */
class Loggerek : Application(){

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        Kotpref.init(this)
        Kotpref.gson = Gson()

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build())
    }
}