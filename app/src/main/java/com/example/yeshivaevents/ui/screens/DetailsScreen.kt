package com.example.yeshivaevents.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.yeshivaevents.ui.state.UIState
import com.example.yeshivaevents.viewmodel.EventsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: EventsViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.detailState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        when (val state = uiState) {
            is UIState.Loading -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is UIState.Empty -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Event not found.") }

            is UIState.Error -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text("Error loading event details.") }

            is UIState.Success -> {
                val event = state.data ?: return@Scaffold

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    if (!event.thumbnail.isNullOrBlank()) {
                        AsyncImage(
                            model = event.thumbnail,
                            contentDescription = event.title ?: "Event image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }

                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = event.title ?: "Untitled Event",
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(8.dp))
                        Text("When: ${event.date?.whenText ?: event.date?.startDate ?: "N/A"}")
                        Spacer(Modifier.height(4.dp))
                        Text("Where: ${event.address?.joinToString(", ") ?: "N/A"}")

                        if (!event.description.isNullOrBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text(event.description, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                                    context.startActivity(intent)
                                }
                            ) {
                                Icon(Icons.Default.OpenInNew, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Open")
                            }

                            OutlinedButton(
                                onClick = {
                                    val share = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "${event.title}\n${event.link}")
                                    }
                                    context.startActivity(Intent.createChooser(share, "Share event"))
                                }
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }
    }
}
