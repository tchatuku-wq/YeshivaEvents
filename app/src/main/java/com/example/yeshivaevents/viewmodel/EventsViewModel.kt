package com.example.yeshivaevents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.mvi.EventAction
import com.example.yeshivaevents.mvi.EventReducer
import com.example.yeshivaevents.mvi.EventState
import com.example.yeshivaevents.repository.EventsRepository
import com.example.yeshivaevents.ui.state.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel(
    private val repository: EventsRepository
) : ViewModel() {

    private val _eventState = MutableStateFlow(EventState())
    val eventState: StateFlow<EventState> = _eventState

    private fun fireAction(action: EventAction) {
        _eventState.value = EventReducer(_eventState.value, action)
    }

    private val _homeState = MutableStateFlow<UIState<List<EventsResult>>>(UIState.Loading)
    val homeState: StateFlow<UIState<List<EventsResult>>> = _homeState

    private val _detailState = MutableStateFlow<UIState<EventsResult?>>(UIState.Loading)
    val detailState: StateFlow<UIState<EventsResult?>> = _detailState

    private val defaultQuery = "yeshiva university events"

    fun refreshHomeScreen(query: String = defaultQuery, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            fireAction(EventAction.LoadEvents)
            _homeState.value = UIState.Loading

            try {
                val events = if (forceRefresh) {
                    repository.refreshEvents(query = query, apiKey = "d78a50095f7b78f49b6c78952c5d95d9ade2eba6349ce391f582d84827d1e2ae")
                } else {
                    repository.getEvents(query = query, apiKey = "d78a50095f7b78f49b6c78952c5d95d9ade2eba6349ce391f582d84827d1e2ae")
                }


                fireAction(EventAction.FetchListOfEvents(events))
                _homeState.value = if (events.isNotEmpty()) UIState.Success(events) else UIState.Empty
            } catch (e: Exception) {
                fireAction(EventAction.Error(e.message ?: "Unknown error while loading events"))
                _homeState.value = UIState.Error(e)
            }
        }
    }

    fun refreshDetailsScreen(link: String) {
        viewModelScope.launch {
            _detailState.value = UIState.Loading
            try {
                val event = repository.getEventByLink(link)
                _detailState.value = if (event != null) UIState.Success(event) else UIState.Empty
            } catch (e: Exception) {
                _detailState.value = UIState.Error(e)
            }
        }
    }

    fun selectEvent(event: EventsResult) {
        fireAction(EventAction.SelectEvent(event))
        _detailState.value = UIState.Success(event)
    }
}
