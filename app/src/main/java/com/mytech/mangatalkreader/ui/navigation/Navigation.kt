package com.mytech.mangatalkreader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mytech.mangatalkreader.ui.screen.LibraryScreen
import com.mytech.mangatalkreader.ui.screen.ReaderScreen
import com.mytech.mangatalkreader.ui.screen.SettingsScreen
import com.mytech.mangatalkreader.ui.screen.CollectionsScreen
import com.mytech.mangatalkreader.ui.screen.SourcesScreen
import com.mytech.mangatalkreader.ui.screen.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("library") {
            LibraryScreen(navController)
        }
        composable("reader/{mangaId}") { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getString("mangaId")?.toLongOrNull() ?: 0L
            ReaderScreen(navController, mangaId)
        }
        composable("collections") {
            CollectionsScreen(navController)
        }
        composable("sources") {
            SourcesScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
    }
}
