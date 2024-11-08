package com.example.app
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondFragment : Fragment(R.layout.activity_fourth_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backtoMenu1 = view.findViewById<ImageButton>(R.id.back_to_menu1)

        backtoMenu1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MenuFragment())
                .commit()
        }

    }
}
