package com.example.weathery.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.weathery.R

class DetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar 세부 설정
        val toolbar = (activity as? AppCompatActivity)?.findViewById<Toolbar>(R.id.toolbar)
        val titleView = toolbar?.findViewById<TextView>(R.id.toolbar_title)

        titleView?.text = "Berlin, Germany" // 지역명 받아와서 설정
        titleView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        // 메뉴 버튼 색상 변경
        toolbar?.navigationIcon?.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.white),
            PorterDuff.Mode.SRC_IN
        )
    }
}