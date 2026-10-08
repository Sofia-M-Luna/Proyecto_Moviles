package com.example.dsy1105_006d_lc7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dsy1105_006d_lc7.ui.navigation.AppNavigation
import com.example.dsy1105_006d_lc7.ui.theme.DSY1105006DLC7Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DSY1105006DLC7Theme {
                AppNavigation()
            }
        }
    }
}

