package com.example.app
import android.os.Bundle
import android.widget.Button
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MenuFragment : Fragment(R.layout.activity_second_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonFragment1 = view.findViewById<Button>(R.id.buttonFragment1)
        val buttonFragment2 = view.findViewById<Button>(R.id.buttonFragment2)

        // Установка слушателя на кнопку для первого фрагмента
        buttonFragment1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FirstFragment())
                .commit()
        }

        // Установка слушателя на кнопку для второго фрагмента
        buttonFragment2.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SecondFragment()) // Можно вернуть SecondFragment или другой фрагмент
                .commit()
        }
    }
}
