// AddLocFragment
package com.example.weathery.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.weathery.R

class AddLocFragment(val onAddCity: (String) -> Unit) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_loc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addButton = view.findViewById<Button>(R.id.add_button)

        // TODO: 클릭 시 도시 추가 기능 구현 예정
        addButton.setOnClickListener {
//            val newCity = cityInput.text.toString()
//            onAddCity(newCity)
        }
    }
}
