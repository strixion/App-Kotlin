package com.example.app
import android.os.Bundle
import android.widget.Button
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class FirstFragment : Fragment(R.layout.activity_third_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backtoMenu = view.findViewById<ImageButton>(R.id.back_to_menu)
        val gotocam = view.findViewById<Button>(R.id.camfrag)
        backtoMenu.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MenuFragment())
                .commit()
        }
        gotocam.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,CameraFragment())
                .commit()
        }

    }
}


