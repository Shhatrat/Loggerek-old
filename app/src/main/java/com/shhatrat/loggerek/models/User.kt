package com.shhatrat.loggerek.models

/**
 * Created by szymon on 16.06.17.
 */
data class User(val username : String,
                val profile_url : String,
                val home_location : String,
                val caches_found : Int,
                val caches_notfound : Int,
                val caches_hidden : Int) {}