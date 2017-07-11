package com.shhatrat.loggerek.adapters

import android.app.Activity
import android.app.getKoin
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.fragments.LogFragment
import com.shhatrat.loggerek.models.SingleLog
import com.shhatrat.loggerek.models.Unsend
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_full_log.view.*
import kotlinx.android.synthetic.main.list_layout.view.*
import kotlin.collections.ArrayList


class LogAdapter(var c: Activity, var lists: ArrayList<String?>, var type : LogFragment.Type) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val realm by lazy{c.getKoin().get<Realm>()}

    fun getList(): ArrayList<String?> {
        return lists
    }

    fun removeAt(id : Int){
        removeFromDb(lists.get(id))
        lists.removeAt(id)
        notifyItemRemoved(id)
        notifyItemRangeChanged(id, lists.size)
    }

    private fun  removeFromDb(get: String?) {
        realm.beginTransaction()
        realm.where(SingleLog::class.java)
                .equalTo("type", type.name)
                .equalTo("log", get)
                .findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    private fun  editLog(get: String?, position: Int){
        MaterialDialog.Builder(c)
                .title("Edit log")
                .positiveText("ok")
                .negativeText("cancel")
                .neutralText("delete")
                .neutralColor(ContextCompat.getColor(c, R.color.md_red_400))
                .onNeutral { dialog, which -> run { removeAt(position); } }
                .inputType(android.text.InputType.TYPE_CLASS_TEXT)
                .input(get, get, com.afollestad.materialdialogs.MaterialDialog.InputCallback { dialog, input -> updateToDb(get ,input.toString(), position) })
                .show()
    }

    private fun  saveToDb(log: String) {
        val toAdd = SingleLog()
        toAdd.saveEnum(type)
        toAdd.log = log
        realm.beginTransaction()
        realm.copyToRealm(toAdd)
        realm.commitTransaction()
     }

    private fun  updateToDb(get: String?, toString: String, position: Int) {
        removeFromDb(get)
        saveToDb(toString)
        lists[position] = toString
        notifyDataSetChanged()
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val v = LayoutInflater.from(c).inflate(R.layout.list_layout, parent, false)
        return Item(v)
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        (holder as Item).bindData(lists[position]!!, type)
        holder.itemView.list_linear_layout.setOnClickListener { editLog(lists[position]!!, position) }
    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: String, type : LogFragment.Type) {
            itemView.textView.text = _list

            when(type){LogFragment.Type.GOOD -> itemView.unsend_row_fab.setImageResource(R.drawable.ic_sentiment_very_satisfied_white_24dp)
                LogFragment.Type.BAD -> itemView.unsend_row_fab.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_white_24dp)
                LogFragment.Type.DEFAULT -> itemView.unsend_row_fab.setImageResource(R.drawable.ic_tab_white_24dp)
            }
        }
    }
}