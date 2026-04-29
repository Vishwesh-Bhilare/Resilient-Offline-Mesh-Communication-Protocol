package com.mesh.app.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mesh.app.ui.chat.ChatScreen
import com.mesh.app.ui.logs.LogsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var onPermissionsGranted: (() -> Unit)? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            onPermissionsGranted?.invoke()
        }
        onPermissionsGranted = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MeshApp(
                onEnableRequested = {
                    val missing = requiredPermissions().filter {
                        checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
                    }
                    if (missing.isEmpty()) {
                        it()
                    } else {
                        onPermissionsGranted = it
                        permissionLauncher.launch(missing.toTypedArray())
                    }
                }
            )
        }
    }

    private fun requiredPermissions(): List<String> = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
            add(Manifest.permission.BLUETOOTH_ADVERTISE)
            // Some OEM builds still gate BLE scan result delivery behind location runtime permission.
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private enum class MainTab { NETWORK, CHAT, LOGS }

@Composable
private fun MeshApp(
    onEnableRequested: ((() -> Unit) -> Unit),
    viewModel: MainViewModel = hiltViewModel()
) {
    val peerCount by viewModel.peerCount.collectAsStateWithLifecycle()
    val isMeshEnabled by viewModel.isMeshEnabled.collectAsStateWithLifecycle()
    val isGateway by viewModel.isGateway.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(MainTab.NETWORK) }

    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = tab == MainTab.NETWORK,
                        onClick = { tab = MainTab.NETWORK },
                        label = { Text("Main") },
                        icon = { Text("●") }
                    )
                    NavigationBarItem(
                        selected = tab == MainTab.CHAT,
                        onClick = { tab = MainTab.CHAT },
                        label = { Text("Chat") },
                        icon = { Text("💬") }
                    )
                    NavigationBarItem(
                        selected = tab == MainTab.LOGS,
                        onClick = { tab = MainTab.LOGS },
                        label = { Text("Logs") },
                        icon = { Text("📡") }
                    )
                }
            }
        ) { innerPadding ->
            when (tab) {
                MainTab.NETWORK -> MainScreen(
                    modifier = Modifier.padding(innerPadding),
                    enabled = isMeshEnabled,
                    peerCount = peerCount,
                    isGateway = isGateway,
                    onToggle = { nextState ->
                        if (nextState) {
                            onEnableRequested { viewModel.setMeshEnabled(true) }
                        } else {
                            viewModel.setMeshEnabled(false)
                        }
                    }
                )

                MainTab.CHAT -> Box(modifier = Modifier.padding(innerPadding)) {
                    ChatScreen()
                }

                MainTab.LOGS -> Box(modifier = Modifier.padding(innerPadding)) {
                    LogsScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    peerCount: Int,
    isGateway: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onToggle(!enabled) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (enabled) "TURN OFF MESH" else "TURN ON MESH",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        }
        Text(
            text = "Nearby devices: $peerCount\nGateway online: ${if (isGateway) "Yes" else "No"}",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}
