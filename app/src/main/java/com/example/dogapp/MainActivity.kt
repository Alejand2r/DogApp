package com.example.dogapp

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity

/**
 * Única Activity de la app. Solo aloja el NavHostFragment (ver res/layout/activity_main.xml)
 * que navega entre los Fragments definidos en res/navigation/nav_graph.xml.
 * Extiende FragmentActivity porque BiometricPrompt (HU 1.0) lo requiere.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContentView(R.layout.activity_main)
    }
}
