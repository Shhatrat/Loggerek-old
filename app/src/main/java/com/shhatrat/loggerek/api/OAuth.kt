package com.shhatrat.loggerek.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
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

    fun prepareOAuth(c : Context) {
        provider.retrieveRequestToken(consu, OAuth.OUT_OF_BAND)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://opencaching.pl/okapi/apps/authorize?oauth_token=${consu.token}"))
        startActivity(c, browserIntent, null)
    }

    fun okHttpPin(pin : String){
        provider.retrieveAccessToken(consu, pin)
        saveConsumer()
    }

    fun saveConsumer() {
        Data.consumerkey = consu.token
        Data.consumerSecret = consu.tokenSecret
    }
}