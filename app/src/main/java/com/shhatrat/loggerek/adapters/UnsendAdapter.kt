package com.shhatrat.loggerek.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.Unsend
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_unsend_row.view.*

/**
 * Created by szymon on 6/30/17.
 */
class UnsendAdapter (var c: Context, var lists: ArrayList<Unsend>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    fun getList(): ArrayList<Unsend> {
        return lists
    }


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
        (holder as Item).bindData(lists[position])
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(u: Unsend) {
            itemView.unsend_row_title.text = "${u.cacheOp} - ${u.type}"
            itemView.unsend_row_conent.text = u.log
        }
    }
}