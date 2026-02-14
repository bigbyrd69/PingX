package com.ping.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ping.app.domain.model.Peer

@Composable
fun PeerCard(peer: Peer) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "ID: ${peer.id}")
            Text(text = "Alias: ${peer.alias}")
            Text(text = "Transport: ${peer.transport}")
            Text(text = "Hop distance: ${peer.hopDistance}")
        }
    }
}
