package com.example.strixionai.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.strixionai.R

class GraphsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)
        view.findViewById<TextView>(R.id.title).text = "Графики и Анализ"
        return view
    }
}
