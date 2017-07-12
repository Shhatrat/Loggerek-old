package com.shhatrat.loggerek.models

/**
 * Created by szymon on 6/29/17.
 */
enum class OcResponse(var message: String) {

    ALREADY_CUBMITTED("You have already submitted a \"Found it\" log entry once. Now you may submit \"Comments\" only!"),
    REQ_PASSWORD("This cache requires a password. You didn't provide one!"),
    SUCESS("Your cache log entry was posted successfully."),
    NO_INTERNET("No access to the Internet"),
    BAD_PASSWORD("Invalid password!")
}