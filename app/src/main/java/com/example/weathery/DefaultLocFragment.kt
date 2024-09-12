// DefaultLocFragment
package com.example.weathery

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class DefaultLocFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_loc, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_detailFragment)
        }
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
