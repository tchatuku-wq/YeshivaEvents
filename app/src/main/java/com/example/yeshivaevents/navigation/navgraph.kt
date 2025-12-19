package com.example.yeshivaevents.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.yeshivaevents.ui.screens.DetailsScreen
import com.example.yeshivaevents.ui.screens.HomeScreen
import com.example.yeshivaevents.viewmodel.EventsViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")

    object Details : Screen("details/{eventLink}") {
        fun createRoute(eventLink: String): String = "details/${Uri.encode(eventLink)}"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: EventsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onEventClick = { event ->
                    // pass the link (primary key) safely
                    navController.navigate(Screen.Details.createRoute(event.link))
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("eventLink") { type = NavType.StringType })
        ) { backStackEntry ->
            val encoded = backStackEntry.arguments?.getString("eventLink") ?: return@composable
            val link = Uri.decode(encoded)

            // Your ViewModel method is refreshDetailsScreen(link)
            viewModel.refreshDetailsScreen(link)

            DetailsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
