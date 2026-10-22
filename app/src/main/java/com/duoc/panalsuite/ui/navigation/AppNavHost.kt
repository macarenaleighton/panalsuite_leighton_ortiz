package com.duoc.panalsuite.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.duoc.panalsuite.ui.SplashScreen
import com.duoc.panalsuite.viewmodel.SessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION_MS)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION_MS)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIMATION_DURATION_MS)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIMATION_DURATION_MS)) }
    ) {
        composable(
            route = Routes.SPLASH,
            exitTransition = { fadeOut(tween(ANIMATION_DURATION_MS)) }
        ) {
            SplashScreen(
                sessionViewModel = sessionViewModel,
                onSessionChecked = { isLoggedIn ->
                    navController.navigate(if (isLoggedIn) Routes.INBOX else Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true } // Atrás no vuelve al splash
                    }
                }
            )
        }

        composable(
            route = Routes.LOGIN,
            enterTransition = { fadeIn(tween(ANIMATION_DURATION_MS)) }
        ) {
            PendingScreen(title = "Login (Etapa 4)")
        }

        composable(Routes.INBOX) {
            PendingScreen(title = "Bandeja (Etapa 5)")
        }
    }
}

@Composable
private fun PendingScreen(
    title: String,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
        if (actionLabel != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

private const val ANIMATION_DURATION_MS = 300
