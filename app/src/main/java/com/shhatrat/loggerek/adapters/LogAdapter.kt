package com.shhatrat.loggerek.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_layout.view.*
import java.util.*


class LogAdapter(var c: Context, var lists: ArrayList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    fun getList(): ArrayList<String> {
        return lists
    }

    fun getClickListener(): Observable<String> {
        return clickSubject
    }
    private val clickSubject = PublishSubject.create<String>()


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        var v = LayoutInflater.from(c).inflate(R.layout.list_layout, parent, false)
        return Item(v)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as Item).bindData(lists[position])
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: String) {
            itemView.textView.text = _list
        }
    }
}