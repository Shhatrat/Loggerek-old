package com.shhatrat.loggerek.api

import com.shhatrat.loggerek.Oo
import com.shhatrat.loggerek.models.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by szymon on 16.06.17.
 */
interface Api {

    @GET("users/user?fields=username")
    fun getUsername()
                :Observable<User>

    @GET("caches/save_personal_notes")
    fun saveNote(@Query("cache_code") code : String,
                 @Query("new_value") note : String)
                : Observable<Void>

    @GET("logs/submit")
    fun logEntry(@Query("logtype") logtype :String,
                 @Query("comment") comment :String)
                : Observable<Void>
}