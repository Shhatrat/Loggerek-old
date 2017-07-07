package com.shhatrat.loggerek.api

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import com.kenny.snackbar.SnackBarItem
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Data
import oauth.signpost.OAuth
import oauth.signpost.basic.DefaultOAuthProvider
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer


/**
 * Created by szymon on 16.06.17.
 */
class OAuth(val consumer_key : String , val consumer_secret : String){

    var provider = DefaultOAuthProvider(
            "https://opencaching.pl/okapi/services/oauth/request_token",
            "https://opencaching.pl/okapi/services/oauth/access_token",
            "https://opencaching.pl/okapi/services/oauth/access_token")

    var consu : OkHttpOAuthConsumer = OkHttpOAuthConsumer(consumer_key, consumer_secret)

    fun prepareOAuth(c : Activity) {
        try {
            provider.retrieveRequestToken(consu, OAuth.OUT_OF_BAND)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://opencaching.pl/okapi/apps/authorize?oauth_token=${consu.token}"))
            startActivity(c, browserIntent, null)
        }catch(e : Throwable){
            c.runOnUiThread({
                SnackBarItem.Builder(c)
                        .setMessage(e.message)
                        .setSnackBarMessageColorResource(R.color.md_black_1000)
                        .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                        .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                        .setDuration(3000)
                        .show()
            })
        }
    }

    fun okHttpPin(pin : String, activity : Activity){
        try {
            provider.retrieveAccessToken(consu, pin)
            saveConsumer()
        }catch (e :Throwable){
            activity.runOnUiThread({
                SnackBarItem.Builder(activity)
                        .setMessage(e.message)
                        .setSnackBarMessageColorResource(R.color.md_black_1000)
                        .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                        .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                        .setDuration(3000)
                        .show()
            })
        }
    }

    fun saveConsumer() {
        Data.consumerkey = consu.token
        Data.consumerSecret = consu.tokenSecret
    }
}