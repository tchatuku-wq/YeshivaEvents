package com.example.yeshivaevents.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.ui.state.UIState
import com.example.yeshivaevents.viewmodel.EventsViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: EventsViewModel,
    onEventClick: (EventsResult) -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.refreshHomeScreen() }
    LaunchedEffect(homeState) { isRefreshing = false }

    val pullRefresh = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.refreshHomeScreen()
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("YeshivaEvents") },
                actions = {
                    IconButton(onClick = {
                        isRefreshing = true
                        viewModel.refreshHomeScreen()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pullRefresh(pullRefresh)
        ) {

            Column(Modifier.fillMaxSize()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = { Text("Search events (title, venue, city)…") },
                    singleLine = true
                )

                when (homeState) {
                    is UIState.Loading -> LoadingView()
                    is UIState.Error -> ErrorView((homeState as UIState.Error).throwable.message ?: "Unknown error")
                    is UIState.Empty -> EmptyView()
                    is UIState.Success -> {
                        val events = (homeState as UIState.Success<List<EventsResult>>).data

                        val filtered = if (query.isBlank()) events else {
                            val q = query.trim().lowercase()
                            events.filter { e ->
                                val hay = buildString {
                                    append(e.title.orEmpty()); append(" ")
                                    append(e.address?.joinToString(" ").orEmpty())
                                }.lowercase()
                                hay.contains(q)
                            }
                        }

                        if (filtered.isEmpty()) {
                            EmptyView(message = "No matches for \"$query\"")
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                items(filtered, key = { it.link }) { event ->
                                    EventCard(event = event, onClick = { onEventClick(event) })
                                }
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefresh,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun EventCard(event: EventsResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(Modifier.padding(12.dp)) {
            if (!event.thumbnail.isNullOrBlank()) {
                AsyncImage(
                    model = event.thumbnail,
                    contentDescription = event.title ?: "Event image",
                    modifier = Modifier.size(88.dp)
                )
                Spacer(Modifier.width(12.dp))
            }

            Column(Modifier.fillMaxWidth()) {
                Text(
                    text = event.title ?: "Untitled event",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val whenLine = event.date?.whenText ?: event.date?.startDate
                if (!whenLine.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(whenLine, style = MaterialTheme.typography.bodyMedium)
                }

                val location = event.address?.joinToString(", ")
                if (!location.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        location,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingView() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator()
}

@Composable
fun ErrorView(message: String) = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text(text = "Error: $message")
}

@Composable
fun EmptyView(message: String = "No events available") =
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message)
    }
