package com.shhatrat.loggerek

import android.app.getKoin
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import com.afollestad.materialdialogs.MaterialDialog
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.api.OAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_config.*
import kotlinx.android.synthetic.main.content_config.*


class ConfigActivity : AppCompatActivity() {

    var autorization = false
    var o : OAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        o = OAuth(getString(R.string.consumer_key), getString(R.string.consumer_secret))
        val ret by lazy {getKoin().get<Api>()}
        preapreFab()

        if(isUserLogged())
            ret.getUsername()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                        u -> config_hello.text= u.username}, {
                        e -> Log.d("dddd", e.message)})
        else
        {
            config_hello.text= getString(R.string.no_configured_user)
        }
    }

    fun isUserLogged(): Boolean = Data.consumerkey!=null

    fun preapreFab(){
        if(isUserLogged()) {
            fab.visibility = GONE
            fabclear.visibility = VISIBLE
        } else
        {
            fab.visibility = VISIBLE
            fabclear.visibility = GONE
        }

        fab.setOnClickListener {
            logUser()
            autorization = true
        }

        fabclear.setOnClickListener {
            Data.userName = null
            Data.consumerkey = null
            Data.consumerSecret = null
            config_hello.text  = getString(R.string.no_configured_user)
            preapreFab()
        }
    }

    fun logUser() {
            AsyncTask.execute {
                o!!.prepareOAuth(this)
            }
    }

    override fun onResume() {
        super.onResume()
        if(autorization) {
            autorization = false
           MaterialDialog.Builder(this)
                    .title("Set code")
                    .positiveText("ok")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("ddd", " ", MaterialDialog.InputCallback { dialog, input -> finishOAuth(input.toString()) })
                    .show()
        }
    }

    fun finishOAuth(dat : String) {
        AsyncTask.execute {
            o!!.okHttpPin(dat)
            val ret by lazy {getKoin().get<Api>()}
            ret.getUsername()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        u -> config_hello.text= u.username}, {
                        e -> Log.d("dddd", e.message)})
        }
    }
}
