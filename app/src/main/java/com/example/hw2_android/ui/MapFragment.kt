package com.example.hw2_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.hw2_android.R
import com.google.android.material.textview.MaterialTextView

class MapFragment : Fragment() {

    private lateinit var map_LBL_title: MaterialTextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        findViews(view)
        return view
    }

    private fun findViews(view: View) {
        map_LBL_title = view.findViewById(R.id.map_LBL_title)
    }

    fun zoom(lat:Double, lon:Double){
        map_LBL_title.text = buildString {
            append("📍\n")
            append(lat)
            append(",\n")
            append(lon)
        }
    }
}