package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.Fragment
import android.view.View.GONE
import android.view.View.VISIBLE
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.fragments.LogFragment
import com.shhatrat.loggerek.fragments.SettingsFragment
import com.shhatrat.loggerek.fragments.StatusFragment
import com.shhatrat.loggerek.fragments.UnsendFragment
import com.shhatrat.loggerek.models.Data
import com.shhatrat.loggerek.models.Unsend
import com.shhatrat.loggerek.models.User
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_config.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.net.URLEncoder


class ConfigActivity : android.support.v7.app.AppCompatActivity() {

    var autorization = false
    var o : com.shhatrat.loggerek.api.OAuth? = null
    val parameters = "username|caches_found|caches_notfound|caches_hidden|profile_url|home_location"
    val ret by lazy {getKoin().get<com.shhatrat.loggerek.api.Api>()}
    val realm by lazy{getKoin().get<Realm>()}

    lateinit  var header : AccountHeader
    lateinit  var drawer : Drawer

    val STATUS = "Status"
    val UNSEND = "Unsend"
    val GOOD = "Good"
    val BAD = "Bad"
    val DEFAULT = "Default"
    val SETTINGS = "Settings"
    val LOGOUT = "Logout"


    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.shhatrat.loggerek.R.layout.activity_config)
        val toolbar = findViewById(com.shhatrat.loggerek.R.id.toolbar) as android.support.v7.widget.Toolbar
        setSupportActionBar(toolbar)

        if(!isViewed())
        startActivity(Intent(this, IntroActivity::class.java))

        o = com.shhatrat.loggerek.api.OAuth(getString(R.string.consumer_key), getString(R.string.consumer_secret))

        preapreFab()
        preapreDrawer()

        if(isUserLogged()) {
            replaceFragment(StatusFragment.getInstance())
            preapreHeader()
        }
    }

    fun updateBadge(){
        drawer.updateBadge(1, StringHolder(realm.where(Unsend::class.java).findAll().count().toString()))
    }

    fun getUnsendNumber() = realm.where(Unsend::class.java).findAll().count().toString()

    fun  isViewed(): Boolean = Data.introViewed

    fun isUserLogged(): Boolean = Data.consumerkey !=null

    fun preapreFab(){
        if(isUserLogged())
            fab.visibility = GONE
        else
            fab.visibility = VISIBLE

        fab.setOnClickListener {
            logUser()
            autorization = true
        }
    }

    fun logUser() {
            android.os.AsyncTask.execute {
                o!!.prepareOAuth(this)
            }
    }

    override fun onResume() {
        super.onResume()
        updateBadge()
        if(autorization) {
            autorization = false
           com.afollestad.materialdialogs.MaterialDialog.Builder(this)
                    .title(getString(R.string.set_code))
                    .positiveText(getString(R.string.ok))
                    .inputType(android.text.InputType.TYPE_CLASS_TEXT)
                    .input("", " ", com.afollestad.materialdialogs.MaterialDialog.InputCallback { _, input -> finishOAuth(input.toString()) })
                    .show()
        }
    }

    fun preapreHeader(){
        header.addProfile(ProfileDrawerItem().withName(realm.where(User::class.java).findFirst().username).withIcon(R.drawable.logo_oc), 0)
    }

    fun preapreDrawer(){
        header = AccountHeaderBuilder()
                .withActivity(this@ConfigActivity)
                .build()

        drawer = DrawerBuilder()
                .withActivity(this@ConfigActivity)
                .withToolbar(this@ConfigActivity.toolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        PrimaryDrawerItem().withName(STATUS).withTag(STATUS).withIcon(R.drawable.ic_sentiment_very_satisfied_white_24dp),
                        PrimaryDrawerItem().withName(UNSEND).withTag(UNSEND).withIcon(R.drawable.ic_clear_white_24dp).withIdentifier(1).withBadge(getUnsendNumber()).withBadgeStyle(BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)),
                        DividerDrawerItem(),
                        PrimaryDrawerItem().withName(GOOD).withTag(GOOD).withIcon(R.drawable.ic_sentiment_very_satisfied_white_24dp),
                        PrimaryDrawerItem().withName(BAD).withTag(BAD).withIcon(R.drawable.ic_sentiment_very_dissatisfied_white_24dp),
                        PrimaryDrawerItem().withName(DEFAULT).withTag(DEFAULT).withIcon(R.drawable.ic_tab_white_24dp)
                        )
                .withOnDrawerItemClickListener { _, _, drawerItem ->  changeFragment(drawerItem) }
                .addStickyDrawerItems(
                        PrimaryDrawerItem().withName(SETTINGS).withTag(SETTINGS).withIcon(R.drawable.ic_settings_white_24dp),
                        PrimaryDrawerItem().withName(LOGOUT).withTag(LOGOUT).withIcon(R.drawable.ic_exit_to_app_white_24dp))
                .build()
    }

    fun changeFragment(drawerItem: IDrawerItem<*, *>?): Boolean{
        if(!isUserLogged())
            showTip()
        else
            {
                toolbar.title = "${getString(R.string.app_name)} - ${drawerItem!!.tag}"
                if(drawerItem.tag == GOOD)
                    replaceFragment(LogFragment.getInstance(LogFragment.Type.GOOD))
                if(drawerItem.tag == BAD)
                    replaceFragment(LogFragment.getInstance(LogFragment.Type.BAD))
                if(drawerItem.tag == DEFAULT)
                    replaceFragment(LogFragment.getInstance(LogFragment.Type.DEFAULT))
                if(drawerItem.tag == STATUS)
                    replaceFragment(StatusFragment.getInstance())
                if(drawerItem.tag == UNSEND)
                    replaceFragment(UnsendFragment.getInstance())
                if(drawerItem.tag == SETTINGS)
                    replaceFragment(SettingsFragment.getInstance())
                if(drawerItem.tag == LOGOUT)
                    logout()
            }
        return false
    }

    fun replaceFragment(fragemnt : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragemnt)
        transaction.commit()
    }


    fun showTip(){
        MaterialTapTargetPrompt.Builder(this)
                .setTarget(findViewById(R.id.fab))
                .setPrimaryText(getString(R.string.add_account))
                .setCaptureTouchEventOnFocal(true)
                .setCaptureTouchEventOutsidePrompt(true)
                .setSecondaryText("")
                .show()
    }

    fun finishOAuth(dat : String) {
        android.os.AsyncTask.execute {
            o!!.okHttpPin(dat)
            ret.getUsername(parameters.getUTF8String())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        u ->
                        run {
                            realm.addUser(u)
                            preapreFab()
                            preapreHeader()
                            replaceFragment(StatusFragment.getInstance())
                        }
                    }, {
                        e -> android.util.Log.d("apiLog", e.message)})

        }
    }


    fun logout(){
        realm.deteleAllWithoutMagic()
        Data.clear()
        preapreFab()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(supportFragmentManager.fragments.get(0))
        transaction.commit()
        header.removeProfile(0)
        updateBadge()
    }
}

fun String.getUTF8String() :String = URLEncoder.encode(this, "UTF-8")

private fun  Realm.addUser(u: User) {
    this.beginTransaction()
    this.copyToRealmOrUpdate(u)
    this.commitTransaction()
}

private fun  Realm.deteleAllWithoutMagic() {
    this.beginTransaction()
    this.deleteAll()
    this.commitTransaction()
}
