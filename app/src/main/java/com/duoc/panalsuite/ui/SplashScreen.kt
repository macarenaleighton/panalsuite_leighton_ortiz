package com.duoc.panalsuite.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duoc.panalsuite.viewmodel.SessionState
import com.duoc.panalsuite.viewmodel.SessionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    sessionViewModel: SessionViewModel,
    onSessionChecked: (Boolean) -> Unit
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()
    var isAnimationDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        isAnimationDone = true
    }

    LaunchedEffect(isAnimationDone, sessionState) {
        if (isAnimationDone && sessionState !is SessionState.Loading) {
            onSessionChecked(sessionState is SessionState.LoggedIn)
        }
    }

    SplashScreenContent()
}

@Composable
fun SplashScreenContent(animate: Boolean = true) {
    val logoScale = remember { Animatable(if (animate) 0.3f else 1f) }
    val textAlpha = remember { Animatable(if (animate) 0f else 1f) }

    LaunchedEffect(Unit) {
        if (!animate) return@LaunchedEffect
        launch { logoScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
        launch {
            delay(300)
            textAlpha.animateTo(1f, tween(durationMillis = 500))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppLogo(
            modifier = Modifier
                .size(120.dp)
                .scale(logoScale.value)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "PanalSuite Mobile",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(textAlpha.value)
        )
        Text(
            text = "Gestión de flujos de trabajo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(textAlpha.value)
        )
    }
}

private const val SPLASH_DURATION_MS = 1200L

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreenContent(animate = false)
}
