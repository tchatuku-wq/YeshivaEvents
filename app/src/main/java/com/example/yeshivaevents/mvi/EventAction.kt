package com.example.yeshivaevents.mvi

import com.example.yeshivaevents.model.EventsResult

sealed class EventAction {


    object LoadEvents : EventAction()


    data class FetchListOfEvents(val events: List<EventsResult>) : EventAction()


    data class SelectEvent(val event: EventsResult) : EventAction()


    data class Error(val message: String) : EventAction()
}
