package com.example.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class TextDisplayFragment : Fragment(R.layout.fragment_text_display) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.textView)
        val backtoMenu = view.findViewById<Button>(R.id.back_to_menu2)
        backtoMenu.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MenuFragment())
                .commit()
        }
        val recognizedText = arguments?.getString("recognizedText") ?: "Нет текста для отображения"
        textView.text = recognizedText
    }
}
