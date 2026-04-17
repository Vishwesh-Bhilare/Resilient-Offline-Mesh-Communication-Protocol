package com.mesh.app.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mesh.app.service.MeshForegroundService
import com.mesh.app.ui.chat.ChatScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startForegroundService(Intent(this, MeshForegroundService::class.java))
        setContent { MeshApp() }
    }
}

@Composable
private fun MeshApp(viewModel: MainViewModel = hiltViewModel()) {
    val nav = rememberNavController()
    val peerCount = viewModel.peerCount.collectAsStateWithLifecycle()
    val isGateway = viewModel.isGateway.collectAsStateWithLifecycle()
    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(navController = nav, startDestination = "chat") {
            composable("chat") { ChatScreen() }
        }
    }
}
