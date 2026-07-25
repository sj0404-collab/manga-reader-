package com.mytech.mangatalkreader.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mytech.mangatalkreader.ui.screens.chat.ChatScreen
import com.mytech.mangatalkreader.ui.screens.library.LibraryScreen
import com.mytech.mangatalkreader.ui.screens.manga_details.MangaDetailsScreen
import com.mytech.mangatalkreader.ui.screens.reader.ReaderScreen
import com.mytech.mangatalkreader.ui.screens.search.SearchScreen
import com.mytech.mangatalkreader.ui.screens.settings.SettingsScreen
import com.mytech.mangatalkreader.ui.screens.sources.SourcesScreen
import com.mytech.mangatalkreader.ui.viewmodel.ReaderViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Library.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToManga = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSources = {
                    navController.navigate(Screen.Sources.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToChat = {
                    navController.navigate(Screen.Chat.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToManga = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Sources.route) {
            SourcesScreen(
                onNavigateToManga = { mangaId ->
                    navController.navigate(Screen.MangaDetails.createRoute(mangaId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen()
        }

        composable(
            route = Screen.Reader.route,
            arguments = listOf(navArgument("chapterId") { type = NavType.LongType })
        ) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getLong("chapterId") ?: 0L
            val viewModel = hiltViewModel<ReaderViewModel>()
            viewModel.loadChapter(chapterId)
            ReaderScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MangaDetails.route,
            arguments = listOf(navArgument("mangaId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mangaId = backStackEntry.arguments?.getLong("mangaId") ?: 0L
            MangaDetailsScreen(
                mangaId = mangaId,
                onNavigateToReader = { chapterId ->
                    navController.navigate(Screen.Reader.createRoute(chapterId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
