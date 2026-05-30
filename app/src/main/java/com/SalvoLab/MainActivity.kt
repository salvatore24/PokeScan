package com.SalvoLab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.SalvoLab.ui.navigation.PokeScanNavHost
import com.SalvoLab.ui.theme.PokeScanTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeScanTheme {
                PokeScanNavHost()
            }
        }
    }
}
