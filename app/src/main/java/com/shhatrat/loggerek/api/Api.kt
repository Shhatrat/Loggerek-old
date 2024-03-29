package com.shhatrat.loggerek.api

import com.shhatrat.loggerek.models.Cache
import com.shhatrat.loggerek.models.Log
import com.shhatrat.loggerek.models.NoteResponse
import com.shhatrat.loggerek.models.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by szymon on 16.06.17.
 */
interface Api {

    @GET("users/user")
    fun getUsername(
            @Query("fields", encoded = true) fields : String)
                :Observable<User>

    @GET("caches/save_personal_notes")
    fun saveNote(@Query("cache_code") code : String,
                 @Query("new_value") note : String)
                : Observable<NoteResponse>

    @GET("logs/submit")
    fun logEntryFull(@Query("cache_code") cache_code:String,
                     @Query("logtype") logtype:String,
                     @Query("comment") comment:String,
                     @Query("when") date:String,
                     @Query("recommend") recomennded: Boolean,
                     @Query("rating") rating: Int?,
                     @Query("password") password: String)
    : Observable<Response<Log>>

    @GET("logs/submit")
    fun logEntry(@Query("cache_code") cache_code :String,
                 @Query("logtype") logtype :String,
                 @Query("comment") comment :String)
            : Observable<Response<Log>>

    @GET("caches/geocache")
    fun geocache(@Query("cache_code") cache_code :String,
                 @Query("fields", encoded = true) fields : String)
                : Observable<Cache>
}