package com.shhatrat.loggerek

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import com.shhatrat.loggerek.api.OAuth
import kotlinx.android.synthetic.main.activity_config.*
import kotlinx.android.synthetic.main.content_config.*


class ConfigActivity : AppCompatActivity() {

    var autorization = false
    var o  = OAuth(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        Kotpref.init(this)
        Kotpref.gson = Gson()


        fab.setOnClickListener {
            if(Data.consumerkey!=null)
            {
                    RetrofitFactory().reqTest()!!.subscribe({
                        u -> config_hello.text=u.username}, {
                        e -> Log.d("dddd", e.message)
                })
            }
            else {
                logUser()
                autorization = true
            }
        }

        if(!isUserLogged())
            config_hello.text = "No configured user!"
        else
            config_hello.text = Data.userName
    }

    fun isUserLogged() : Boolean = Data.userName!=null
    fun logUser() {
            AsyncTask.execute {
//                o.preapareRequestToken(this)  //todo
                o.prepareOAuth(this)
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
            o.okHttpPin(dat)
        }
    }

     override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_config, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
