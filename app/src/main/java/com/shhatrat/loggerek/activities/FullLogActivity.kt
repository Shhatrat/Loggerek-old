package com.shhatrat.loggerek.activities

import android.app.getKoin
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.api.LogHandler
import com.shhatrat.loggerek.models.Cache
import com.shhatrat.loggerek.models.LogRequest
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_full_log.*
import kotlinx.android.synthetic.main.activity_full_log_fab.*
import java.text.SimpleDateFormat
import java.util.*


class FullLogActivity : AbstractActivity() {

    val sharedPrefs by lazy{getKoin().get<SharedPreferences>()}
    val retrofit by lazy { getKoin().get<Api>() }
    var reco = false
    var passToNote = true
    var date : String = getData()
    var isPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_log_fab)
        preapreSaveNote()
        if(intentWithCache())
            loadCache()
        else {
            checkIntent()
            downloadCache(getOpFormIntent()!!)
        }
        preapreListeners()
        preapreData()
    }

    private fun loadCache() {
        val parcel = intent.getParcelableExtra<LogRequest>("unsend")
        downloadCache(parcel.cache_code!!)
    }

    private fun  intentWithCache(): Boolean {
        return intent.getParcelableExtra<LogRequest>("unsend") != null
    }

    private fun preapreData() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd\tHH:mm")
        full_date.text = df.format(c.time)
    }

    private fun getData(): String{
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        return df.format(c.time)
    }

    private fun preapreSaveNote(){
        passToNote = !sharedPreferences.getBoolean("normal_save_password", false)
        changePass()
    }

    private fun preapreListeners() {
        full_image_reco.setOnClickListener { changeReco() }
        full_text_reco.setOnClickListener { changeReco()  }
        full_fab.setOnClickListener { fabListener() }
        full_date.setOnClickListener { showDataPicker()  }
    }

    private fun showDataPicker() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> run { date ="$year-${monthOfYear.preapreZeros()}-${dayOfMonth.preapreZeros()}" ; showTimePicker() } },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker(){
    val now = Calendar.getInstance()
    val dpd = TimePickerDialog.newInstance(
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second -> run {
                full_date.text = "$date\t$hourOfDay:$minute"

                val c = Calendar.getInstance()
                val df = SimpleDateFormat(".SSSZ")

                date = "${date}T${hourOfDay.preapreZeros()}:${minute.preapreZeros()}:${second.preapreZeros()}${df.format(c.time)}"
            } },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
    )
    dpd.show(fragmentManager, "Datepickerdialog")
    }

    fun Int.preapreZeros():String{
        if(this.toString().length==1)
            return "0$this"
        return this.toString()
    }

    fun isFoundLogType() = full_logtype.getItems<String>().get(full_logtype.selectedIndex) == getString(R.string.found_it)

    fun fabListener(){
        if(isFoundLogType() && isPassword && full_password.text.isNullOrEmpty()) {
            full_password.setBackgroundColor(ContextCompat.getColor(this, R.color.md_red_700))
            return
        }
        full_password.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark))
        val request = LogRequest(getOpFormIntent()!!,
                full_logtype.getItems<String>().get(full_logtype.selectedIndex),
                full_log.text.toString(),
                date,
                reco,
                getRating(),
                full_password.text.toString())

        var note : String? = null
        if(passToNote || !full_password.text.isNullOrEmpty())
            note = full_password.text.toString()

        retrofit.logEntryFull(getOpFormIntent()!!,
                full_logtype.getItems<String>().get(full_logtype.selectedIndex),
                full_log.text.toString(),
                date,
                reco,
                getRating(),
                full_password.text.toString())
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe({
                    u -> LogHandler(this).success(request, u, note )}, {
                    e -> LogHandler(this).error(request, e)})

        if(!full_password.text.isNullOrEmpty()){
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

    private fun  getRating(): Int? {
        if(full_rating.rating.toInt()==0)
            return null
        return full_rating.rating.toInt()
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

    fun setReco(){
        if(reco)
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp))
        else
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white))
    }

    fun downloadCache(op : String){
        getOpFormIntent()
        retrofit.geocache(op, "name|location|type|recommendations|founds|req_passwd".getUTF8String())
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
        loadData()
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
        isPassword = u.req_passwd
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
        preapreRecomendationListener(u.type=="Event")
        loadData()
    }

    private fun loadData() {
        if(intentWithCache()) {
            val parcel = intent.getParcelableExtra<LogRequest>("unsend")
            full_log.setText(parcel.comment ?: "")
            val rating = parcel.rating ?: 0
            full_rating.rating = rating.toFloat()

            reco = parcel.recommend ?: false
            setReco()
            val list  = full_logtype.getItems<String>()
            list.forEachIndexed { index, s -> if(parcel.logtype?:"" == s) full_logtype.selectedIndex = index }

            full_password.setText(parcel.password?:"")
        }
    }

    private fun preapreRecomendationListener(event : Boolean) {
        full_logtype.setOnItemSelectedListener { view, position, id, item ->
            if(item == getString(R.string.found_it) && !event){
                full_password.visibility= View.VISIBLE
                full_send_password.visibility = View.VISIBLE
                full_text_send_password.visibility= View.VISIBLE

                full_rating.visibility = View.VISIBLE
                full_image_reco.visibility = View.VISIBLE
                full_text_reco.visibility = View.VISIBLE
            }else
            {
                full_password.visibility= View.GONE
                full_send_password.visibility = View.GONE
                full_text_send_password.visibility= View.GONE

                full_rating.visibility = View.GONE
                full_image_reco.visibility = View.GONE
                full_text_reco.visibility = View.GONE
            }
            if(item == getString(R.string.attended) && event)
                full_rating.visibility = View.VISIBLE
        }
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
        val second = "&markers=color:red%7Clabel:%7C${home_location!!.replace("|", ",")}&zoom=14&size=500x200&maptype=roadmap&key="
        val key = getString(R.string.google_maps_key)
        if(!home_location.isNullOrBlank())
            return "$first${home_location!!.replace("|", ",")}$second$key"
        else
            return "${first}51.743792, 19.450380&zoom=6&size=600x300&maptype=roadmap&key=$key"
    }
}
