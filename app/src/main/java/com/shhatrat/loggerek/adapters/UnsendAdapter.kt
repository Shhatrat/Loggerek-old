package com.shhatrat.loggerek.adapters

import android.app.Activity
import android.app.getKoin
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.activities.FullLogActivity
import com.shhatrat.loggerek.activities.getUTF8String
import com.shhatrat.loggerek.api.LogHandler
import com.shhatrat.loggerek.models.Unsend
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import kotlinx.android.synthetic.main.list_unsend_row.view.*

/**
 * Created by szymon on 6/30/17.
 */
class UnsendAdapter (var c: Activity, var lists: ArrayList<Unsend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    fun getList(): ArrayList<Unsend> {
        return lists
    }

    val retrofit by lazy {c.getKoin().get<com.shhatrat.loggerek.api.Api>()}
    val realm by lazy {c.getKoin().get<Realm>()}

    fun getClickListener(): Observable<String> {
        return clickSubject
    }
    private val clickSubject = PublishSubject.create<String>()
    lateinit var v : View

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
         v = LayoutInflater.from(c).inflate(R.layout.list_unsend_row, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    fun removeAt(id : Int){
        removeFromDb(lists[id].cacheOp!!, lists[id].log!!, lists[id].timestamp!!)
        lists.removeAt(id)
        this@UnsendAdapter.notifyItemRemoved(id)
        this@UnsendAdapter.notifyItemRangeChanged(id, this@UnsendAdapter.lists.size)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            retrofit.geocache(lists[position].cacheOp!!, "name".getUTF8String())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        u ->
                        (holder as ViewHolder).bindData(lists[position], u.name)
                    }, {
                        (holder as ViewHolder).bindData(lists[position])
                    })

            holder?.itemView?.unsend_constraint_layout?.setOnClickListener {
                showDialog(lists[position], position)
            }
            holder?.itemView?.unsend_row_fab?.setOnClickListener {
                showDialog(lists[position], position)
            }
    }

    private fun editLog(unsend: Unsend) {
        val intent = Intent(c, FullLogActivity::class.java)
        intent.putExtra("unsend", unsend.getParcel())
        c.startActivity(intent)
    }

    private fun tryAgain(unsend: Unsend) {
        retrofit.logEntry(unsend.cacheOp!!, unsend.logtype!!, unsend.log!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> run{
                    LogHandler(c).success(unsend.getParcel(), u)
                }}, {
                    e ->
                    run {
                        LogHandler(c).error(unsend.getParcel(), e) }
                })    }

    private fun  showDialog(unsend: Unsend, position: Int) {
        MaterialDialog.Builder(c)
                .title("Unsend log")
                .content("Select action")
                .negativeText("cancel")
                .neutralText("delete")
                .neutralColor(ContextCompat.getColor(c, R.color.md_red_400))
                .onNeutral { dialog, which -> run {
//                    removeFromDb(unsend.cacheOp!!, unsend.log!!, unsend.timestamp!!)
                    removeAt(position)
                }}
                .items(listOf("Edit log", "Try again"))
                .itemsCallbackSingleChoice(-1, MaterialDialog.ListCallbackSingleChoice { dialog, itemView, which, text ->
                    if(text == "Edit log")
                        editLog(unsend)
                    if(text == "Try again")
                        tryAgain(unsend)
                    return@ListCallbackSingleChoice true
                })
                .show()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(u: Unsend, cacheName : String = u.cacheOp!!) {
            itemView.unsend_row_title.text = "$cacheName - ${u.type}"
            itemView.unsend_row_conent.text = u.log
        }
    }

    private fun  removeFromDb(cacheOp: String, log : String, timestamp : Long) {
        realm.beginTransaction()
        realm.where(Unsend::class.java)
                .equalTo("cacheOp", cacheOp)
                .equalTo("log", log)
                .equalTo("timestamp", timestamp)
                .findFirst().deleteFromRealm()
        realm.commitTransaction()
    }
}