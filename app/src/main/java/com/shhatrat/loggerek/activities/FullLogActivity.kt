package com.shhatrat.loggerek.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.kenny.snackbar.SnackBarItem
import com.kenny.snackbar.SnackBarListener
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.di.StupidSingleton
import com.shhatrat.loggerek.models.LogRequest
import com.shhatrat.loggerek.models.translateResponse
import com.shhatrat.loggerek.presenters.FullLogPresenter
import com.shhatrat.loggerek.presenters.FullLogPresenterImpl
import com.shhatrat.loggerek.presenters.FullLogView
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_full_log.*
import kotlinx.android.synthetic.main.activity_full_log_fab.*
import java.text.SimpleDateFormat
import java.util.*


class FullLogActivity : AbstractActivity(), FullLogView {

    var dialog : MaterialDialog? = null

    override fun startLoading() {
        if(dialog == null){
           dialog = MaterialDialog.Builder(this)
                    .title(getString(R.string.uploading_log))
                    .content("")
                    .theme(Theme.LIGHT)
                    .progress(true, 0)
                    .build()
        }else{
            dialog!!.show()
        }
    }

    override fun stopLoading() {
        dialog?.let { dialog!!.dismiss() }
    }

    override fun changeLogSpinnerSelected(item: String?) {
        item?.let{
            full_logtype.getItems<String>().forEachIndexed { index, s -> if(item?:"" == s) full_logtype.selectedIndex = index }}
    }

    var reco = false
    var date : String = getData()
    var passToNote = true

    override fun changeRecoText(text: String?) {
        full_text_reco.text = text
    }

    override fun changeTitle(title: String?) {
        title?.let{ full_title.text = title }
    }

    override fun changeOnClickTitleListener(address: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(address))
        full_title.setOnClickListener {         ContextCompat.startActivity(this, browserIntent, null) }
        full_map.setOnClickListener {         ContextCompat.startActivity(this, browserIntent, null) }
        full_icon_type.setOnClickListener {         ContextCompat.startActivity(this, browserIntent, null) }
    }

    override fun changeMap(address: String?) {
        Picasso.with(this).load(address).into(full_map)
    }

    override fun changeIconCacheType(cacheType: String?) {
        cacheType?.let {
            when(cacheType){
            "Traditional" ->    full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.traditionals))
            "Multi" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.multi))
            "Quiz" ->           full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.quiz))
            "Other" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.unknown))
            "Own" ->            full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.owncache))
            "Moving" ->         full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.moving))
            "Event" ->          full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.event))
            "Virtual" ->        full_icon_type.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.virtual))
        }}
    }

    override fun changeLog(log: String?) {
        log?.let {full_log.setText(log)}
    }

    override fun changeRatesVisibility(visible: Boolean?) {
        if(visible?:true){
                full_rating.visibility = View.VISIBLE
                full_image_reco.visibility = View.VISIBLE
                full_text_reco.visibility = View.VISIBLE
        }else{
            full_rating.visibility = View.GONE
            full_image_reco.visibility = View.GONE
            full_text_reco.visibility = View.GONE
        }
    }

    override fun changeRates(rate: Int?) {
        rate?.let{full_rating.rating = rate.toFloat()}
    }

    override fun changeReco(recommend: Boolean?) {
        if(recommend?:true)
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white_24dp))
        else
            full_image_reco.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp))
    }

    override fun changeRecoVisibility(visible: Boolean?) {
        if(visible?:true) {
            full_rating.visibility = View.VISIBLE
            full_image_reco.visibility = View.VISIBLE
            full_text_reco.visibility = View.VISIBLE
        }else{
            full_rating.visibility = View.GONE
            full_image_reco.visibility = View.GONE
            full_text_reco.visibility = View.GONE
        }
    }

    override fun changeLogSpinnerItems(list: List<String>?) {
        list?.let{full_logtype.setItems(list)}
    }

    override fun changeLogSpinnerPosition(list: List<String>?, position: Int?) {
        list?.let{
            full_logtype.setItems(list)
            full_logtype.selectedIndex = position?:0
        }
    }

    override fun changeDate(date: String?) {
      date?.let { full_date.text = date }
    }

    override fun changePassword(password: String?) {
        password?.let { full_password.setText(password) }
    }

    override fun changePasswordVisibility(visible: Boolean) {
            if(visible){
                full_password.visibility= View.VISIBLE
            }else{
                full_password.visibility= View.GONE
            }
    }

    override fun changeSavePassword(save: Boolean?) {
        if(save?:true)
            full_send_password.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white_24dp))
        else
            full_send_password.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_clear_white_24dp))
     }

    override fun changeSavePasswordVisibility(visible: Boolean) {
        if(visible){
            full_send_password.visibility = View.VISIBLE
            full_text_send_password.visibility= View.VISIBLE
        }else{
            full_send_password.visibility = View.GONE
            full_text_send_password.visibility= View.GONE
        }
    }



    override fun showNotification(text: String?, type: String?, finishAfterShow : Boolean) {
        SnackBarItem.Builder(this)
                .setMessage(text!!.translateResponse(this))
                .setSnackBarMessageColorResource(R.color.md_black_1000)
                .setSnackBarBackgroundColorResource(R.color.md_white_1000)
                .setInterpolatorResource(android.R.interpolator.accelerate_decelerate)
                .setSnackBarListener(object : SnackBarListener {
                    override fun onSnackBarStarted(`object`: Any?) {
                    }

                    override fun onSnackBarFinished(`object`: Any?, actionPressed: Boolean) {
                        if(finishAfterShow)
                        finish()
                    }})
                .setDuration(3000)
                .show()
    }

    override fun redPassword() {
        full_password.setBackgroundColor(ContextCompat.getColor(this, R.color.md_red_700))
    }

    override fun normalPassword() {
        full_password.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark))
    }


    private val presenter: FullLogPresenter<FullLogView> by lazy {
        FullLogPresenterImpl(this, retrofit)
    }

    val retrofit by lazy { StupidSingleton.ocApi(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_log_fab)
        preapreListeners()
        if (intentWithCache())
            presenter.getDataFromDb(intent.getParcelableExtra<LogRequest>("unsend"))
        else
            presenter.getDataFromIntent(getOpFormIntent()!!)
    }

    private fun preapreListeners(){
        preapreData()
        full_fab.setOnClickListener{
            val request = LogRequest(getOpFormIntent()!!,
                    full_logtype.getItems<String>()[full_logtype.selectedIndex],
                full_log.text.toString(),
                date,
                reco,
                getRating(),
                full_password.text.toString())
            presenter.send(request, passToNote, full_note.text.toString())
        }

        full_send_password.setOnClickListener { changePassToNote() }
        full_text_send_password.setOnClickListener { changePassToNote() }
        full_image_reco.setOnClickListener { changeReco() }
        full_text_reco.setOnClickListener { changeReco()  }
        full_date.setOnClickListener { showDataPicker()  }
        full_logtype.setOnItemSelectedListener { view, position, id, item -> presenter.changeLogType(item.toString()) }
    }

    private fun preapreData() {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd\tHH:mm")
        full_date.text = df.format(c.time)
    }
    fun changePassToNote(){
        passToNote =  passToNote.not()
        changeSavePassword(passToNote)
    }

    private fun  getRating(): Int? {
        if(full_rating.rating.toInt()==0)
            return null
        return full_rating.rating.toInt()
    }

    fun changeReco(){
        reco = reco.not()
        changeReco(reco)
    }

    private fun  intentWithCache(): Boolean {
        return intent.getParcelableExtra<LogRequest>("unsend") != null
    }



        private fun showDataPicker() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> run { date ="$year-${monthOfYear.prepareZeros()}-${dayOfMonth.prepareZeros()}" ; showTimePicker() } },
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
                date = "${date}T${hourOfDay.prepareZeros()}:${minute.prepareZeros()}:${second.prepareZeros()}${df.format(c.time)}"
            } },
            now.get(Calendar.HOUR_OF_DAY),
            now.get(Calendar.MINUTE),
            true
    )
    dpd.show(fragmentManager, "Datepickerdialog")
    }

    fun Int.prepareZeros():String{
        if(this.toString().length==1)
            return "0$this"
        return this.toString()
    }

    private fun getData(): String{
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        return df.format(c.time)
    }
}