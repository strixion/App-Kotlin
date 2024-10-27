package com.example.strixionai

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.strixionai.ui.GraphsFragment
import com.example.strixionai.ui.ProfileFragment
import com.example.strixionai.ui.ScannerFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkCameraPermission()) {
        } else {
            requestCameraPermission()
        }

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_scanner -> ScannerFragment()
                R.id.navigation_graphs -> GraphsFragment()
                R.id.navigation_profile -> ProfileFragment()
                else -> ScannerFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit()
            true
        }
        navView.selectedItemId = R.id.navigation_scanner
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
            }
        }
    }
}
