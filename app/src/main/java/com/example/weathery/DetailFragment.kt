package com.example.weathery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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

        // toolbar 업데이트
        val toolbarUpdater = activity as? ToolbarUpdater
        toolbarUpdater?.updateToolbar(R.drawable.tb_back, "Seoul, Korea", R.color.white, R.drawable.tb_search_wh)

    }
}