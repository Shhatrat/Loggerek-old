package com.shhatrat.loggerek

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.facebook.stetho.Stetho
import com.google.gson.Gson
import com.shhatrat.loggerek.di.Module
import com.uphyca.stetho_realm.RealmInspectorModulesProvider
import io.realm.Realm
import org.koin.Koin
import org.koin.KoinContext
import org.koin.android.KoinContextAware
import org.koin.android.init

/**
 * Created by szymon on 16.06.17.
 */
class Loggerek : Application(), KoinContextAware{
    override fun getKoin() : KoinContext = context


    lateinit var context : KoinContext

    override fun onCreate() {
        super.onCreate()

        context = Koin().init(this).build(Module())
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