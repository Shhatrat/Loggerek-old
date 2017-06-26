package com.shhatrat.loggerek.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.fragments.LogFragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.list_layout.view.*
import kotlin.collections.ArrayList


class LogAdapter(var c: Context, var lists: ArrayList<String?>, var type : LogFragment.Type) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    fun getList(): ArrayList<String?> {
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
        (holder as Item).bindData(lists[position]!!, type)
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: String, type : LogFragment.Type) {
            itemView.textView.text = _list

            when(type){LogFragment.Type.GOOD -> itemView.f_icon.setImageResource(R.drawable.ic_sentiment_very_satisfied_white_24dp)
                LogFragment.Type.BAD -> itemView.f_icon.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_white_24dp)
                LogFragment.Type.DEFAULT -> itemView.f_icon.setImageResource(R.drawable.ic_tab_white_24dp)
            }
        }
    }
}