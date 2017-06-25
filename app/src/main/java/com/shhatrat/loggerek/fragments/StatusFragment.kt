package com.shhatrat.loggerek.fragments

import android.app.getKoin
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shhatrat.loggerek.R
import com.shhatrat.loggerek.models.User
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_status.*

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
        status_n_found.text = user.caches_found.toString()
        status_n_hidden.text = user.caches_hidden.toString()
        status_n_unfound.text = user.caches_notfound.toString()
        Picasso.with(this.context).load(preapreGoogleMapsLink(user.home_location)).into(ppppp)
    }

    fun  preapreGoogleMapsLink(home_location: String?): String{
        val first = "https://maps.googleapis.com/maps/api/staticmap?center="
        val second = "&zoom=13&size=600x300&maptype=roadmap&key="
        val key = getString(R.string.google_maps_key)
        if(!home_location.isNullOrBlank())
            return "$first${home_location!!.replace("|", ",")}$second$key"
        else
            return "${first}51.743792, 19.450380&zoom=6&size=600x300&maptype=roadmap&key=$key"
    }

    companion object{
        fun getInstance() : StatusFragment {
            return StatusFragment()
        }
    }

}