package com.duoc.panalsuite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.duoc.panalsuite.ui.navigation.AppNavHost
import com.duoc.panalsuite.ui.theme.PanalSuiteTheme
import com.duoc.panalsuite.viewmodel.InboxViewModel
import com.duoc.panalsuite.viewmodel.LoginViewModel
import com.duoc.panalsuite.viewmodel.SessionViewModel

class MainActivity : ComponentActivity() {

    private val sessionViewModel: SessionViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val inboxViewModel: InboxViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PanalSuiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavHost(
                        navController = navController,
                        sessionViewModel = sessionViewModel,
                        loginViewModel = loginViewModel,
                        inboxViewModel = inboxViewModel
                    )
                }
            }
        }
    }
}