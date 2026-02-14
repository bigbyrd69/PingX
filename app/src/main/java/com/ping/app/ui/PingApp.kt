package com.ping.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ping.app.ui.screens.MessageScreen
import com.ping.app.ui.screens.PeerScreen
import com.ping.app.ui.screens.SosScreen

enum class PingTab(val label: String) {
    SOS("SOS"),
    MESSAGES("Messages"),
    PEERS("Peers")
}

@Composable
fun PingApp(viewModel: PingViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(PingTab.SOS) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                PingTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab.label) },
                        icon = {}
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            PingTab.SOS -> SosScreen(viewModel = viewModel, contentPadding = padding)
            PingTab.MESSAGES -> MessageScreen(viewModel = viewModel, contentPadding = padding)
            PingTab.PEERS -> PeerScreen(viewModel = viewModel, contentPadding = padding)
        }
    }
}
