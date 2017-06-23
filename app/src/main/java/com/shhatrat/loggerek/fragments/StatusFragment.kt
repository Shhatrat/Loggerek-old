package com.shhatrat.loggerek.fragments


import android.app.getKoin
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.User
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_status.*


/**
 * A simple [Fragment] subclass.
 */
class StatusFragment : Fragment() {

    val realm by lazy{activity.getKoin().get<Realm>()}

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_status, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = realm.where(User::class.java).findFirst()
        status_nick.text =  user.username
        status_content.text = user.home_location?:"u"
        status_n_found.text = user.caches_found.toString()?:"u"
        status_n_hidden.text = user.caches_hidden.toString()?:"u"
        status_n_unfound.text = user.caches_notfound.toString()?:"u"
    }

    companion object{
        fun getInstance() : StatusFragment {
            return StatusFragment()
        }
    }

}