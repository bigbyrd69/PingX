package com.ping.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ping.app.domain.model.MeshPacket
import com.ping.app.domain.model.PacketPayload

@Composable
fun MessageList(messages: List<MeshPacket>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        messages.forEach { packet ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Type: ${packet.packetType}", style = MaterialTheme.typography.labelLarge)
                    Text(text = "Status: ${packet.status}")
                    Text(text = "TTL: ${packet.ttl} | Hops: ${packet.hopCount}")
                    Text(text = "Body: ${packet.bodyText()}")
                }
            }
        }
    }
}

private fun MeshPacket.bodyText(): String {
    return when (val payloadValue = payload) {
        is PacketPayload.Text -> payloadValue.body
        is PacketPayload.Location -> "Lat ${payloadValue.coordinates.latitude}, Lng ${payloadValue.coordinates.longitude}"
        PacketPayload.Sos -> "Emergency SOS"
    }
}
