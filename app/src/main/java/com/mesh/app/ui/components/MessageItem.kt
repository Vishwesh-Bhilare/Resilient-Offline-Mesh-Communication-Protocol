package com.mesh.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mesh.app.core.protocol.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(message: Message, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxWidth().padding(8.dp), tonalElevation = 2.dp) {
        androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
            Text(message.content, style = MaterialTheme.typography.bodyLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "${message.sender.take(8)} • ${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(message.timestamp * 1000))}",
                    style = MaterialTheme.typography.bodySmall
                )
                AssistChip(onClick = {}, label = { Text("hops:${message.hops}") })
            }
        }
    }
}
