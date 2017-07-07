package com.shhatrat.loggerek.adapters

import android.app.Activity
import android.app.getKoin
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.activities.getUTF8String
import com.shhatrat.loggerek.models.Unsend
import io.reactivex.Observable
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
        try {
            retrofit.geocache(lists[position].cacheOp!!, "name".getUTF8String())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe({
                        u ->
                        (holder as ViewHolder).bindData(lists[position], u.name)
                    }, {
                        (holder as ViewHolder).bindData(lists[position])
                    })
        }catch (e : Throwable){
            holder?.itemView?.visibility = View.GONE
        }
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