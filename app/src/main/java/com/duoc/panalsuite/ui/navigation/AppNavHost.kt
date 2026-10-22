package com.duoc.panalsuite.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.duoc.panalsuite.ui.InboxScreen
import com.duoc.panalsuite.ui.LoginScreen
import com.duoc.panalsuite.ui.SplashScreen
import com.duoc.panalsuite.ui.TicketDetailScreen
import com.duoc.panalsuite.ui.TicketFormScreen
import com.duoc.panalsuite.viewmodel.InboxViewModel
import com.duoc.panalsuite.viewmodel.LoginViewModel
import com.duoc.panalsuite.viewmodel.SessionViewModel
import com.duoc.panalsuite.viewmodel.TicketDetailViewModel
import com.duoc.panalsuite.viewmodel.TicketFormViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    loginViewModel: LoginViewModel,
    inboxViewModel: InboxViewModel,
    ticketFormViewModel: TicketFormViewModel,
    ticketDetailViewModel: TicketDetailViewModel
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
                    ticketFormViewModel.reset()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INBOX) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.TICKET_FORM) {
            TicketFormScreen(
                viewModel = ticketFormViewModel,
                onTicketCreated = { ticketId ->
                    navController.navigate(Routes.ticketDetail(ticketId)) {
                        popUpTo(Routes.INBOX)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TICKET_DETAIL,
            arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: 0L
            TicketDetailScreen(
                viewModel = ticketDetailViewModel,
                ticketId = ticketId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private const val ANIMATION_DURATION_MS = 300