package com.shhatrat.loggerek.di

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.shhatrat.loggerek.R.string.consumer_key
import com.shhatrat.loggerek.R.string.consumer_secret
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.models.Data
import io.realm.Realm
import io.realm.RealmConfiguration
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.AndroidModule
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

/**
 * Created by szymon on 16.06.17.
 */
class Module : AndroidModule(){
    override fun context() =
            declareContext {
                provide { createLoggingIntercpetor()}
                provide { createOAuth(resources.getString(consumer_key), resources.getString(consumer_secret)) }
                provide { createClient(get(),get())}
                provide { retrofit(get()) }
                provide { ocApi(get()) }
                provide { realm()}
                provide { sharedPreferences(get()) }
            }

    fun sharedPreferences(c : Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(c)
    }

    fun realm() : Realm{
            val config = RealmConfiguration.Builder().build()
            return Realm.getInstance(config)
    }

    fun createLoggingIntercpetor(): HttpLoggingInterceptor {
        val httplogin = HttpLoggingInterceptor()
        httplogin.level = HttpLoggingInterceptor.Level.BODY
            return httplogin
    }

    fun createOAuth(key : String,  secret :String): SigningInterceptor {
        val okCunsumer =  OkHttpOAuthConsumer(key, secret)
        okCunsumer.setTokenWithSecret(Data.consumerkey, Data.consumerSecret)
        return SigningInterceptor(okCunsumer)
    }

    private fun createClient(logger: HttpLoggingInterceptor, consumer : SigningInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(consumer)
                .build()
    }

    private fun retrofit(client : OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://opencaching.pl/okapi/services/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
    }

    private fun  ocApi(retrofit : Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

}