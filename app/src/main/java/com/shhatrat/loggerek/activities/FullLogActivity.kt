package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.models.Cache
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_full_log.*
import kotlinx.android.synthetic.main.activity_full_log_fab.*

class FullLogActivity : AbstractActivity() {

    val retrofit by lazy { getKoin().get<Api>() }
    var reco = false
    var passToNote = true
    var date : String = System.currentTimeMillis().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_log_fab)
        downloadCache()
        preapreListeners()
    }

    private fun preapreListeners() {
        full_image_reco.setOnClickListener { changeReco() }
        full_text_reco.setOnClickListener { changeReco()  }
        full_fab.setOnClickListener { fabListener() }
    }

    fun fabListener(){
        retrofit.logEntryFull(getOpFormIntent()!!,
                full_logtype.getItems<String>().get(full_logtype.selectedIndex),
                full_log.text.toString(),
                date,
                reco,
                full_rating.numStars)
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe({
//                    u -> setupData(u)
                }, {
//                    e -> setupOfflineData(getOpFormIntent())
                })
        val text = full_note.text.toString()
        if(passToNote || text != ""){
            retrofit.saveNote(getOpFormIntent()!!, full_note.text.toString())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        //                    u -> setupData(u)
                    }, {
                        //                    e -> setupOfflineData(getOpFormIntent())
                    })
        }
    }

    fun changeReco(){
        if(reco) {
            reco=false
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp))
        }else {
            reco=true
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white))
        }
    }

    fun downloadCache(){
        getOpFormIntent()
        retrofit.geocache(getOpFormIntent()!!, "name|location|type|recommendations|founds|req_passwd".getUTF8String())
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe({
                    u -> setupData(u)
                }, {
                    e -> setupOfflineData(getOpFormIntent())
                })
    }

    private fun  setupOfflineData(opFormIntent: String?) {
        full_title.text = opFormIntent!!
        preapreSpinner()
    }

    fun preapreSpinner(){
        val list = listOf(
                getString(R.string.found_it),
                getString(R.string.will_attend),
                getString(R.string.attended),
                getString(R.string.comment),
                getString(R.string.didnt_fint_it))
        full_logtype.setItems(list)
    }

    fun preapreSpinner(event : Boolean){
        if(event) {
            val list = listOf(
                    getString(R.string.will_attend),
                    getString(R.string.attended),
                    getString(R.string.comment))
            full_logtype.setItems(list)
        }
        else{
            val list = listOf(
                    getString(R.string.found_it),
                    getString(R.string.comment),
                    getString(R.string.didnt_fint_it))
            full_logtype.setItems(list)
        }
    }

    private fun  setupData(u: Cache) {
        full_title.text = u.name
        Picasso.with(this).load(preapreGoogleMapsLink(u.location)).into(full_map)
        full_text_reco.text = "Add recommendation (${u.recommendations}/${u.founds})"
        if(!u.req_passwd) {
            full_password.visibility= View.GONE
            full_send_password.visibility = View.GONE
            full_text_send_password.visibility= View.GONE
        }
        if(u.type=="Event")
        {
            full_image_reco.visibility= View.GONE
            full_text_reco.visibility= View.GONE
        }
        else{
            preapreSaveNoteListener()
        }
        when(u.type){
            "Traditional" ->    full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.traditionals))
            "Multi" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.multi))
            "Quiz" ->           full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.quiz))
            "Other" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unknown))
            "Own" ->            full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.owncache))
            "Moving" ->         full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.moving))
            "Event" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.event))
            "Virtual" ->        full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.virtual))
        }
        preapreSpinner(u.type=="Event")
    }

    private fun preapreSaveNoteListener() {
        full_send_password.setOnClickListener { changePass() }
        full_text_send_password.setOnClickListener { changePass()  }
    }

    private fun changePass() {
        if(passToNote) {
            passToNote=false
            full_send_password.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp))
        }else {
            passToNote=true
            full_send_password.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white))
        }
    }

    fun  preapreGoogleMapsLink(home_location: String?): String{
        val first = "https://maps.googleapis.com/maps/api/staticmap?center="
        val second = "&markers=color:red%7Clabel:C%7C${home_location!!.replace("|", ",")}&zoom=14&size=500x200&maptype=roadmap&key="
        val key = getString(R.string.google_maps_key)
        if(!home_location.isNullOrBlank())
            return "$first${home_location!!.replace("|", ",")}$second$key"
        else
            return "${first}51.743792, 19.450380&zoom=6&size=600x300&maptype=roadmap&key=$key"
    }
}
