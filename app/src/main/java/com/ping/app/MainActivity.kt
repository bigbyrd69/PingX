package com.ping.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ping.app.data.repository.MeshRepositoryImpl
import com.ping.app.ui.PingApp
import com.ping.app.ui.PingViewModel
import com.ping.app.ui.theme.PingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PingRoot()
        }
    }
}

@Composable
private fun PingRoot() {
    val appContext = LocalContext.current.applicationContext
    val viewModel = remember { PingViewModel(MeshRepositoryImpl(appContext)) }
    PingTheme {
        PingApp(viewModel = viewModel)
    }
}
