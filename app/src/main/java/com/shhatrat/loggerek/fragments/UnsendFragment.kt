package com.shhatrat.loggerek.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.shhatrat.loggerek.R


/**
 * A simple [Fragment] subclass.
 */
class UnsendFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_unsend, container, false)
    }

}
