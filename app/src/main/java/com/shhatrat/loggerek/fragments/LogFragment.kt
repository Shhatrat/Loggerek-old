package com.shhatrat.loggerek.fragments


import android.app.getKoin
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.adapters.LogAdapter
import com.shhatrat.loggerek.models.SingleLog
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener
import io.reactivex.disposables.Disposable
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_log.*

class LogFragment : Fragment() {

    enum class Type{
        GOOD, BAD, DEFAULT
    }

    private var subscribe: Disposable? = null
    lateinit var type : Type
    val realm by lazy{activity.getKoin().get<Realm>()}


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

    fun refreshDataAndRecycleView(){
        log_recycle_view.adapter = LogAdapter(this.context, getList(), type)
        log_recycle_view.adapter.notifyDataSetChanged()
        getList()
    }

    private fun setupRecycleView() {
        log_recycle_view.layoutManager = LinearLayoutManager(this.context)
        log_recycle_view.hasFixedSize()
        log_recycle_view.setOnTouchListener(preapreListener(log_recycle_view))
        log_recycle_view.adapter = LogAdapter(this.context, getList(), type)
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

    fun preapreListener(recyclerView :RecyclerView): SwipeDismissRecyclerViewTouchListener? {

        return SwipeDismissRecyclerViewTouchListener.Builder(
                recyclerView,
                object : SwipeDismissRecyclerViewTouchListener.DismissCallbacks{
                    override fun onDismiss(view: View?) {
                        val id = recyclerView.getChildAdapterPosition(view)
                        val adapter =  recyclerView.adapter as LogAdapter
                        removeFromDb(adapter.lists.get(id))
                        adapter.lists.removeAt(id)
                        log_recycle_view.adapter = LogAdapter(this@LogFragment.context, getList(), type)
                    }

                    override fun canDismiss(p0: Int): Boolean {
                        return true
                    }
                })
                .setItemClickCallback({
                    u ->
                    run {
                        try {
                            val adapter = recyclerView.adapter as LogAdapter
                            editLog(adapter.lists[u])
                        }catch (e :Throwable){}
                    }
                })
                .setIsVertical(false)
                .create()
    }

    private fun  removeFromDb(get: String?) {
        realm.beginTransaction()
        realm.where(SingleLog::class.java)
                .equalTo("type", type.name)
                .equalTo("log", get)
                .findAll().deleteAllFromRealm()
        realm.commitTransaction()
    }

    private fun  editLog(get: String?){
        MaterialDialog.Builder(this.context)
                .title("Edit log")
                .positiveText("ok")
                .negativeText("cancel")
                .neutralText("delete")
                .neutralColor(ContextCompat.getColor(context, R.color.md_red_400))
                .onNeutral { dialog, which -> run { removeFromDb(get); refreshDataAndRecycleView() } }
                .inputType(android.text.InputType.TYPE_CLASS_TEXT)
                .input(get, get, com.afollestad.materialdialogs.MaterialDialog.InputCallback { dialog, input -> updateToDb(get ,input.toString()) })
                .show()
    }

    private fun  updateToDb(get: String?, toString: String) {
        removeFromDb(get)
        saveToDb(toString)
    }
}
