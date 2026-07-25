package com.mytech.mangatalkreader.ui.navigation

sealed class Screen(val route: String) {
    data object Library : Screen("library")
    data object Search : Screen("search")
    data object Sources : Screen("sources")
    data object Settings : Screen("settings")
    data object Chat : Screen("chat")
    data object Reader : Screen("reader/{chapterId}") {
        fun createRoute(chapterId: Long) = "reader/$chapterId"
    }
    data object MangaDetails : Screen("manga_details/{mangaId}") {
        fun createRoute(mangaId: Long) = "manga_details/$mangaId"
    }
}
