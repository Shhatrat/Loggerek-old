package com.shhatrat.loggerek.models

/**
 * Created by szymon on 06.07.17.
 */
data class LogRequest(val cache_code : String?,
                      val logtype : String? =null,
                      val comment : String? =null,
                      val logDate : String? =null,
                      val recommend : Boolean? =null,
                      val rating : Int? =null,
                      val password : String? =null){
}