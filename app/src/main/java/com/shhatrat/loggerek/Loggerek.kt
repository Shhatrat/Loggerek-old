package com.shhatrat.loggerek

import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.shhatrat.loggerek.di.Module
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import org.koin.android.KoinApplication

/**
 * Created by szymon on 16.06.17.
 */
class Loggerek : KoinApplication(Module::class){

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