package com.shhatrat.loggerek.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.adapters.UnsendAdapter
import com.shhatrat.loggerek.di.StupidSingleton
import com.shhatrat.loggerek.models.Unsend
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_unsend.*


/**
 * A simple [Fragment] subclass.
 */
class UnsendFragment : Fragment() {

    val realm by lazy{StupidSingleton.realm()}

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_unsend, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        setupRecycleView()
        unsend_swipe_to_refresh.setOnRefreshListener {
            unsend_swipe_to_refresh.isRefreshing = true
            setupRecycleView() }
    }

    private fun setupRecycleView() {
        unsend_swipe_to_refresh.isRefreshing = false
        unsend_recycleview.layoutManager = LinearLayoutManager(this.context)
        unsend_recycleview.hasFixedSize()
        ItemTouchHelper(getHelper(unsend_recycleview)).attachToRecyclerView(unsend_recycleview)
        unsend_recycleview.adapter = UnsendAdapter(this.activity, getListFromDB())
    }


    fun getHelper(recyclerView : RecyclerView): ItemTouchHelper.SimpleCallback {
        var simpleTouch = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                return  true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                        val adapter =  recyclerView.adapter as UnsendAdapter
                        adapter.removeAt(viewHolder!!.adapterPosition)
            }
        }
        return simpleTouch
    }

    private fun getListFromDB(): ArrayList<Unsend> {
     return   ArrayList(realm.where(Unsend::class.java).findAll().toList())
    }

    companion object{
        fun getInstance() : UnsendFragment {
            return UnsendFragment()
        }
    }
}
