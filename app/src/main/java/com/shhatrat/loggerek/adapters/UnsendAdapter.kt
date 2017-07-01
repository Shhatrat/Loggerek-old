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
import kotlinx.android.synthetic.main.list_unsend_row.view.*

/**
 * Created by szymon on 6/30/17.
 */
class UnsendAdapter (var c: Activity, var lists: ArrayList<Unsend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    fun getList(): ArrayList<Unsend> {
        return lists
    }

    val retrofit by lazy {c.getKoin().get<com.shhatrat.loggerek.api.Api>()}


    fun getClickListener(): Observable<String> {
        return clickSubject
    }
    private val clickSubject = PublishSubject.create<String>()


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var v = LayoutInflater.from(c).inflate(R.layout.list_unsend_row, parent, false)
        return Item(v)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        retrofit.geocache(lists[position].cacheOp!! ,"name".getUTF8String())
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe({
                    u ->
                    (holder as Item).bindData(lists[position], u.name)
                }, {
                    (holder as Item).bindData(lists[position])
                })
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(u: Unsend, cacheName : String = u.cacheOp!!) {
            itemView.unsend_row_title.text = "$cacheName - ${u.type}"
            itemView.unsend_row_conent.text = u.log
        }
    }
}