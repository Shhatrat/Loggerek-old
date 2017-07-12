package com.shhatrat.loggerek.models

import android.content.Context
import com.shhatrat.loggerek.R

/**
 * Created by szymon on 6/29/17.
 */
enum class OcResponse(var message: String) {

    ALREADY_CUBMITTED("You have already submitted a \"Found it\" log entry once. Now you may submit \"Comments\" only!"),
    REQ_PASSWORD("This cache requires a password. You didn't provide one!"),
    SUCESS("Your cache log entry was posted successfully."),
    NO_INTERNET("No access to the Internet"),
    BAD_PASSWORD("Invalid password!");

}

public fun String?.translateResponse(c : Context) : String?{
    if(this.isNullOrEmpty())
        return this
    when(this) {
        OcResponse.ALREADY_CUBMITTED.message -> c.getString(R.string.already_submitted)
        OcResponse.REQ_PASSWORD.message -> c.getString(R.string.response_password)
        OcResponse.SUCESS.message -> c.getString(R.string.response_success)
        OcResponse.NO_INTERNET.message -> c.getString(R.string.response_no_internet)
        OcResponse.BAD_PASSWORD.message -> c.getString(R.string.response_bad_password)
    }
    return this
}