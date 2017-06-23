package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.pawegio.kandroid.textWatcher
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Data
import kotlinx.android.synthetic.main.activity_config.*
import kotlinx.android.synthetic.main.content_config.*


class ConfigActivity : android.support.v7.app.AppCompatActivity() {

    var autorization = false
    var o : com.shhatrat.loggerek.api.OAuth? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.shhatrat.loggerek.R.layout.activity_config)
        val toolbar = findViewById(com.shhatrat.loggerek.R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)

        if(!isViewed())
        startActivity(Intent(this, IntroActivity::class.java))


        preapreDrawer()
        o = com.shhatrat.loggerek.api.OAuth(getString(R.string.consumer_key), getString(R.string.consumer_secret))
        val ret by lazy {getKoin().get<com.shhatrat.loggerek.api.Api>()}
        preapreFab()

        if(isUserLogged())
            ret.getUsername()
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe({
                        u -> config_hello.text= u.username}, {
                        e -> android.util.Log.e("apiLog", e.message)})
        else
        {
            config_hello.text= getString(com.shhatrat.loggerek.R.string.no_configured_user)
        }

        et_good.setText(Data.goodLog)
        et_good.textWatcher {
            onTextChanged { text, start, before, count -> Data.goodLog = text.toString()  }
        }

        et_bad.setText(Data.badLog)
        et_bad.textWatcher {
            onTextChanged { text, start, before, count -> Data.badLog = text.toString()  }
        }

        et_log.setText(Data.defaultLog)
        et_log.textWatcher {
            onTextChanged { text, start, before, count -> Data.defaultLog = text.toString()  }
        }
    }

    fun  isViewed(): Boolean = Data.introViewed

    fun isUserLogged(): Boolean = Data.consumerkey !=null

    fun preapreFab(){
        if(isUserLogged()) {
            fab.visibility = GONE
            f_mylog.visibility = VISIBLE
        } else
        {
            fab.visibility = VISIBLE
            f_mylog.visibility = GONE
        }

        fab.setOnClickListener {
            logUser()
            autorization = true
        }

        f_mylog.setOnClickListener {
            Data.userName = null
            Data.consumerkey = null
            Data.consumerSecret = null
            config_hello.text  = getString(com.shhatrat.loggerek.R.string.no_configured_user)
            preapreFab()
        }
    }

    fun logUser() {
            android.os.AsyncTask.execute {
                o!!.prepareOAuth(this)
            }
    }

    override fun onResume() {
        super.onResume()
        if(autorization) {
            autorization = false
           com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                    .title("Set code")
                    .positiveText("ok")
                    .inputType(android.text.InputType.TYPE_CLASS_TEXT)
                    .input("", " ", com.afollestad.materialdialogs.MaterialDialog.InputCallback { dialog, input -> finishOAuth(input.toString()) })
                    .show()
        }
    }

    fun preapreDrawer(){

        var header = AccountHeaderBuilder()
                .withActivity(this@ConfigActivity)
                .addProfiles(
                        ProfileDrawerItem().withName("test").withIcon(R.mipmap.ic_launcher)
                ).build()

        DrawerBuilder()
                .withActivity(this@ConfigActivity)
                .withToolbar(this@ConfigActivity.toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        PrimaryDrawerItem().withName("Unsend").withIcon(R.drawable.ic_clear_white_24dp),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withName("Good").withIcon(R.drawable.ic_sentiment_very_satisfied_white_24dp),
                        PrimaryDrawerItem().withName("Bad").withIcon(R.drawable.ic_sentiment_very_dissatisfied_white_24dp),
                        PrimaryDrawerItem().withName("Default").withIcon(R.drawable.ic_tab_white_24dp)
                        )
                .addStickyDrawerItems(
                        PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_settings_white_24dp),
                        PrimaryDrawerItem().withName("Settings").withIcon(R.drawable.ic_exit_to_app_white_24dp))
                .build()
    }


    fun finishOAuth(dat : String) {
        android.os.AsyncTask.execute {
            o!!.okHttpPin(dat)
            val ret by lazy {getKoin().get<com.shhatrat.loggerek.api.Api>()}
            ret.getUsername()
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        u -> config_hello.text= u.username}, {
                        e -> android.util.Log.d("apiLog", e.message)})
        }
    }
}
