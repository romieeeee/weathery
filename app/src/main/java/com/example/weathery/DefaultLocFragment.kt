// DefaultLocFragment
package com.example.weathery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DefaultLocFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_loc, container, false)
    }

    companion object {
        fun newInstance(city: String): DefaultLocFragment {
            val fragment = DefaultLocFragment()
            val args = Bundle()
            args.putString("city", city)
            fragment.arguments = args
            return fragment
        }
    }
}
