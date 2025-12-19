package com.example.yeshivaevents.mvi

import com.example.yeshivaevents.ui.state.UIState

fun EventReducer(state: EventState, action: EventAction): EventState {
    return when (action) {
        is EventAction.LoadEvents -> state.copy(
            uiState = UIState.Loading,
            error = null
        )

        is EventAction.FetchListOfEvents -> {
            val newUiState = if (action.events.isEmpty()) {
                UIState.Empty
            } else {
                UIState.Success(action.events)
            }

            state.copy(
                uiState = newUiState,
                events = action.events,
                error = null
            )
        }

        is EventAction.SelectEvent -> state.copy(
            selectedEvent = action.event,
            error = null
        )

        is EventAction.Error -> {
            val throwable = Throwable(action.message)
            state.copy(
                uiState = UIState.Error(throwable),
                error = throwable
            )
        }
    }
}
