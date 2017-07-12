package com.shhatrat.loggerek.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.adapters.LogAdapter
import com.shhatrat.loggerek.di.StupidSingleton
import com.shhatrat.loggerek.models.SingleLog
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_log.*

class LogFragment : Fragment() {

    enum class Type{
        GOOD, BAD, DEFAULT
    }

    lateinit var type : Type
    val realm by lazy{StupidSingleton.realm()}


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
        setupFab()
    }

    private fun setupFab() {
        fab_add_log.setOnClickListener {
            MaterialDialog.Builder(this.context)
                    .title("Add log")
                    .positiveText("ok")
                    .negativeText("cancel")
                    .inputType(android.text.InputType.TYPE_CLASS_TEXT)
                    .input("", " ", com.afollestad.materialdialogs.MaterialDialog.InputCallback { dialog, input -> saveToDb(input.toString()) })
                    .show()
        }
    }

    private fun  saveToDb(log: String) {
        val toAdd = SingleLog()
        toAdd.saveEnum(type)
        toAdd.log = log
        realm.beginTransaction()
        realm.copyToRealm(toAdd)
        realm.commitTransaction()
        refreshDataAndRecycleView()
    }


    fun getHelper(recyclerView : RecyclerView): ItemTouchHelper.SimpleCallback {
        var simpleTouch = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return  true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val adapter =  recyclerView.adapter as LogAdapter
                adapter.removeAt(viewHolder!!.adapterPosition)
            }
        }
        return simpleTouch
    }

    fun refreshDataAndRecycleView(){
        log_recycle_view.adapter = LogAdapter(this.activity, getList(), type)
        log_recycle_view.adapter.notifyDataSetChanged()
        getList()
    }

    private fun setupRecycleView() {
        log_recycle_view.layoutManager = LinearLayoutManager(this.context)
        log_recycle_view.hasFixedSize()
        ItemTouchHelper(getHelper(log_recycle_view)).attachToRecyclerView(log_recycle_view)
        log_recycle_view.adapter = LogAdapter(this.activity, getList(), type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type =  Type.valueOf(arguments.getString("type"))
    }

    fun getList() : ArrayList<String?> {
        val list =  realm.where(SingleLog::class.java)
                .equalTo("type", type.name)
                .findAll()
                .toList()
        return ArrayList(list.map { e -> e.log }.toList())
    }

    companion object{
        fun getInstance(t : Type) : LogFragment {
            val fr = LogFragment()
            val args = Bundle()
            args.putString("type", t.name)
            fr.arguments = args
            return fr
        }
    }
}
