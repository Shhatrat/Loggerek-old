package com.shhatrat.loggerek.models

/**
 * Created by szymon on 7/1/17.
 */
data class Cache(
        val name : String,
        val location : String,
        val type : String,
        val recommendations : Int,
        val founds : Int,
        val req_passwd : Boolean){
}