package com.mesh.app.ui.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mesh.app.util.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsScreen(viewModel: LogsViewModel = hiltViewModel()) {
    val logs = viewModel.presenceLogs.collectAsStateWithLifecycle()
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Mesh Logs", style = MaterialTheme.typography.headlineSmall)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(logs.value, key = { it.id }) { message ->
                val time = formatter.format(Date(message.timestamp * 1000L))
                val id = message.content.removePrefix(Constants.PING_PREFIX)
                Text(
                    text = "$time • ${id.take(12)}… • hops=${message.hops}",
                    modifier = Modifier.padding(vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
