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
import com.duoc.panalsuite.ui.InboxScreen
import com.duoc.panalsuite.ui.LoginScreen
import com.duoc.panalsuite.ui.SplashScreen
import com.duoc.panalsuite.viewmodel.InboxViewModel
import com.duoc.panalsuite.viewmodel.LoginViewModel
import com.duoc.panalsuite.viewmodel.SessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    loginViewModel: LoginViewModel,
    inboxViewModel: InboxViewModel
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
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.LOGIN,
            enterTransition = { fadeIn(tween(ANIMATION_DURATION_MS)) }
        ) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    loginViewModel.reset()
                    navController.navigate(Routes.INBOX) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.INBOX) {
            InboxScreen(
                viewModel = inboxViewModel,
                onTicketClick = { ticketId -> navController.navigate(Routes.ticketDetail(ticketId)) },
                onCreateTicketClick = { navController.navigate(Routes.TICKET_FORM) },
                onLogout = {
                    sessionViewModel.logout()
                    inboxViewModel.onStatusSelected(null)
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INBOX) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.TICKET_FORM) {
            PendingScreen(
                title = "Nueva solicitud (Etapa 6)",
                actionLabel = "Volver",
                onAction = { navController.popBackStack() }
            )
        }

        composable(Routes.TICKET_DETAIL) {
            PendingScreen(
                title = "Detalle (Etapa 7)",
                actionLabel = "Volver",
                onAction = { navController.popBackStack() }
            )
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