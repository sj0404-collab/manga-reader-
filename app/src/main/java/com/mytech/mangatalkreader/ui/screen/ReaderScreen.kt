package com.mytech.mangatalkreader.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mytech.mangatalkreader.ui.viewmodel.ReaderViewModel

@Composable
fun ReaderScreen(
    navController: NavController,
    mangaId: Long,
    viewModel: ReaderViewModel = hiltViewModel()
) {
    var showControls by remember { mutableStateOf(true) }
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val showPageNumber by viewModel.showPageNumber.collectAsState()
    val readingMode by viewModel.readingMode.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (showControls) {
                ReaderTopBar(
                    onBackClick = { navController.popBackStack() },
                    onInfoClick = { }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Страница $currentPage / $totalPages",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }

            if (showControls) {
                ReaderBottomBar(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    showPageNumber = showPageNumber,
                    onPreviousPage = { viewModel.previousPage() },
                    onNextPage = { viewModel.nextPage() },
                    onPageChanged = { viewModel.goToPage(it) }
                )
            }
        }
    }
}

@Composable
fun ReaderTopBar(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Text(
            text = "Читалка",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onInfoClick) {
            Icon(Icons.Default.Info, contentDescription = "Info")
        }

        IconButton(onClick = { }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }
    }
}

@Composable
fun ReaderBottomBar(
    currentPage: Int,
    totalPages: Int,
    showPageNumber: Boolean,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onPageChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousPage) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "Previous")
        }

        if (showPageNumber) {
            Text(
                text = "$currentPage / $totalPages",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        } else {
            Box(modifier = Modifier.weight(1f))
        }

        IconButton(onClick = onNextPage) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "Next")
        }
    }
}
