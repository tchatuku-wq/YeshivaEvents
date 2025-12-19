package com.example.yeshivaevents.mvi

import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.ui.state.UIState

data class EventState(
    val uiState: UIState<List<EventsResult>> = UIState.Loading,
    val events: List<EventsResult> = emptyList(),
    val selectedEvent: EventsResult? = null,
    val error: Throwable? = null
)
