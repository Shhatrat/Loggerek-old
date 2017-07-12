package com.shhatrat.loggerek.activities

import android.os.Bundle
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.di.StupidSingleton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NoteActivity : AbstractActivity() {

    lateinit var note : String
    val retrofit by lazy { StupidSingleton.ocApi(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        checkConfiguration()
        checkIntent()
        showDialog()
    }

    fun showDialog() {
        MaterialDialog.Builder(this)
                .positiveText(getString(R.string.send))
                .title(getString(R.string.add_note_to_cache))
                .inputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE)
                .input(getString(R.string.note), "", MaterialDialog.InputCallback { dialog, input -> send(input.toString()) })
                .negativeText(getString(R.string.cancel))
                .onNegative { dialog, which -> finish() }
                .theme(Theme.LIGHT)
                .show()
    }

    fun send(note : String){
        val md = MaterialDialog.Builder(this)
                    .title(getString(R.string.sending))
                    .content(getString(R.string.please_wait))
                    .progress(true, 0)
                    .theme(Theme.LIGHT)
                    .show()
        retrofit.saveNote(getOpFormIntent()!!, note)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { s -> run { md.dismiss(); sucess() } },
                        { e -> run{ md.dismiss(); error(e.message)}})
    }

    //todo saved error
    fun error(message: String?) {
        MaterialDialog.Builder(this)
                .title(getString(R.string.loggerek_error))
                .content(message!!)
                .theme(Theme.LIGHT)
                .positiveText(getString(R.string.ok))
                .onPositive { dialog, which -> finish() }
                .show()
    }

    fun sucess(){
        MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.sending_note_sucessfull))
                .theme(Theme.LIGHT)
                .positiveText(getString(R.string.ok))
                .onPositive { dialog, which -> finish() }
                .show()
    }
}
