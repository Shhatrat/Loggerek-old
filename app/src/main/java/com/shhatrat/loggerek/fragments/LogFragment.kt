package com.shhatrat.loggerek.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.adapters.LogAdapter
import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_log.*

class LogFragment : Fragment() {

    enum class Type{
        GOOD, BAD, DEFAULT
    }

    private var subscribe: Disposable? = null
    lateinit var type : Type

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log_recycle_view.layoutManager = LinearLayoutManager(this.context)
        log_recycle_view.hasFixedSize()
        log_recycle_view.setOnTouchListener(oo(log_recycle_view))
        log_recycle_view.adapter = LogAdapter(this.context, getList())
        setupItemClick()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type =  Type.valueOf(arguments.getString("type"))
    }

    fun getList() : ArrayList<String>{
        var lists = ArrayList<String>()
        lists.add("JAVA")
        lists.add("KOTLIN")
        lists.add("PHP")
        return lists;
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

    private fun setupItemClick() {
        val adapter=  log_recycle_view.adapter as LogAdapter
        subscribe = adapter.getClickListener()
                .subscribe({
                    Toast.makeText(this.context, "Clicked on $it", Toast.LENGTH_LONG).show()
                })
    }



    fun oo(recyclerView :RecyclerView): SwipeDismissRecyclerViewTouchListener? {

        var ff = SwipeDismissRecyclerViewTouchListener.Builder(
                recyclerView,
                object : SwipeDismissRecyclerViewTouchListener.DismissCallbacks{
                    override fun onDismiss(view: View?) {
                        val id = recyclerView.getChildAdapterPosition(view)
                        val adapter =  recyclerView.adapter as LogAdapter
                        adapter.lists.removeAt(id)
                        adapter.notifyDataSetChanged()
                    }

                    override fun canDismiss(p0: Int): Boolean {
                        return true
                    }
                })
                .setItemClickCallback {
                    SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack {
                        Log.d("ddd0", recyclerView.getChildAdapterPosition(view).toString())
                        val adapter =  recyclerView.adapter as LogAdapter
                        Log.d("ddd0dd", adapter.getList().get(recyclerView.getChildAdapterPosition(view)))
                    }
                }
                .setIsVertical(false)
//                .setItemTouchCallback(SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack())
//                .setItemTouchCallback {
//                    SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack {
//                        Log.d("ddd0", recyclerView.getChildAdapterPosition(view).toString())
//                        val adapter =  recyclerView.adapter as LogAdapter
//                        Log.d("ddd0dd", adapter.getList().get(recyclerView.getChildAdapterPosition(view)))
//                    }
//                }
                .create()

        return ff
                }
}
