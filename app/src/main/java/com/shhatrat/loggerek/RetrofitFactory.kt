package com.shhatrat.loggerek

import android.content.Context
import android.util.Log
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.models.User
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

/**
 * Created by szymon on 16.06.17.
 */
class RetrofitFactory{

    fun reqTest(context : Context): Observable<User>? {
        val httplogin = HttpLoggingInterceptor()
        httplogin.level = HttpLoggingInterceptor.Level.BODY

        var okCunsumer =  OkHttpOAuthConsumer(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret))
        okCunsumer.setTokenWithSecret(Data.consumerkey, Data.consumerSecret)

        val client = OkHttpClient.Builder()
                .addInterceptor(httplogin)
                .addInterceptor(SigningInterceptor(okCunsumer))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://opencaching.pl/okapi/services/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()

    return    retrofit.create(Api::class.java).getUsername()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
    }
}