package com.shhatrat.loggerek.fragments


import android.app.getKoin
import android.content.Intent
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
import com.shhatrat.loggerek.activities.FullLogActivity
import com.shhatrat.loggerek.adapters.UnsendAdapter
import com.shhatrat.loggerek.api.Api
import com.shhatrat.loggerek.api.LogHandler
import com.shhatrat.loggerek.models.Unsend
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_unsend.*


/**
 * A simple [Fragment] subclass.
 */
class UnsendFragment : Fragment() {

    val realm by lazy{activity.getKoin().get<Realm>()}
    val retrofit by lazy { activity.getKoin().get<Api>() }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_unsend, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycleView()
    }

    private fun setupRecycleView() {
        unsend_recycleview.layoutManager = LinearLayoutManager(this.context)
        unsend_recycleview.hasFixedSize()
        unsend_recycleview.setOnTouchListener(preapreListener(unsend_recycleview))
        unsend_recycleview.adapter = UnsendAdapter(this.activity, getListFromDB())
    }

    fun preapreListener(recyclerView : RecyclerView): SwipeDismissRecyclerViewTouchListener? {
        return SwipeDismissRecyclerViewTouchListener.Builder(
                recyclerView,
                object : SwipeDismissRecyclerViewTouchListener.DismissCallbacks{
                    override fun onDismiss(view: View?) {
                        val id = recyclerView.getChildAdapterPosition(view)
                        val adapter =  recyclerView.adapter as UnsendAdapter
                        view?.visibility = View.GONE
                        adapter.removeAt(id)
                    }

                    override fun canDismiss(p0: Int): Boolean {
                        return true
                    }
                })
                .setItemClickCallback({
                    u ->
                    run {
                        val adapter = recyclerView.adapter as UnsendAdapter
                        showDialog(adapter.lists[u])
                }})
                .setIsVertical(false)
                .create()
    }

    private fun  showDialog(unsend: Unsend) {
        MaterialDialog.Builder(this.context)
                .title("Unsend log")
                .content("Select action")
                .negativeText("cancel")
                .neutralText("delete")
                .neutralColor(ContextCompat.getColor(context, R.color.md_red_400))
                .onNeutral { dialog, which -> run {
//                    removeFromDb(unsend.cacheOp!!, unsend.log!!, unsend.timestamp!!)
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

    private fun editLog(unsend: Unsend) {
        val intent = Intent(this@UnsendFragment.activity, FullLogActivity::class.java)
        intent.putExtra("unsend", unsend.getParcel())
           startActivity(intent)
    }

    private fun tryAgain(unsend: Unsend) {
        retrofit.logEntry(unsend.cacheOp!!, unsend.logtype!!, unsend.log!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    u -> run{
                    LogHandler(activity).success(unsend.getParcel(), u)
                }}, {
                    e ->
                    run {
                        LogHandler(activity).error(unsend.getParcel(), e) }
                })    }

    private fun getListFromDB(): ArrayList<Unsend> {
     return   ArrayList(realm.where(Unsend::class.java).findAll().toList())
    }

    companion object{
        fun getInstance() : UnsendFragment {
            return UnsendFragment()
        }
    }
}
