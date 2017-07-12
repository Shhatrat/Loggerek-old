package com.shhatrat.loggerek.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.models.Data
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

/**
 * Created by szymon on 12.07.17.
 */
object StupidSingleton {

    private var sharedPrefs : SharedPreferences? = null

        fun sharedPreferences(c : Context): SharedPreferences {
            sharedPrefs?.let { return sharedPrefs!! }
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c)
            return sharedPrefs!!
    }

    private var realm : Realm? = null

    fun realm() : Realm {
        realm?.let { return realm!! }
            val config = RealmConfiguration.Builder().build()
            realm = Realm.getInstance(config)
        return realm!!
    }

    fun createLoggingIntercpetor(): HttpLoggingInterceptor {
        val httplogin = HttpLoggingInterceptor()
        httplogin.level = HttpLoggingInterceptor.Level.BODY
            return httplogin
    }

    fun createOAuth(c : Context): SigningInterceptor {
        val okCunsumer =  OkHttpOAuthConsumer(c.getString(R.string.consumer_key), c.getString(R.string.consumer_secret))
        okCunsumer.setTokenWithSecret(Data.consumerkey, Data.consumerSecret)
        return SigningInterceptor(okCunsumer)
    }

    private fun createClient(c : Context): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(createLoggingIntercpetor())
                .addInterceptor(createOAuth(c))
                .build()
    }

    private fun retrofit(c : Context): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://opencaching.pl/okapi/services/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createClient(c))
                .build()
    }

    private var ocApi : Api? = null

    fun  ocApi(c : Context): Api {
        ocApi?.let { return ocApi!! }
        ocApi =  retrofit(c).create(Api::class.java)
        return ocApi!!
    }
}