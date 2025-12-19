package com.example.yeshivaevents.repository

import com.example.yeshivaevents.data.EventDao
import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.network.EventApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventsRepository(
    private val api: EventApiService,
    private val dao: EventDao
) {

    suspend fun getEvents(query: String, apiKey: String): List<EventsResult> = withContext(Dispatchers.IO) {
        val cached = dao.getAllEvents()
        if (cached.isNotEmpty()) return@withContext cached

        val response = api.getEvents(query = query, apiKey = apiKey)
        val events = response.events_results ?: emptyList()

        if (events.isNotEmpty()) dao.insertEvents(events)
        events
    }

    suspend fun refreshEvents(query: String, apiKey: String): List<EventsResult> = withContext(Dispatchers.IO) {
        val response = api.getEvents(query = query, apiKey = apiKey)
        val events = response.events_results ?: emptyList()

        dao.clear()
        if (events.isNotEmpty()) dao.insertEvents(events)
        events
    }

    suspend fun getEventByLink(link: String): EventsResult? = withContext(Dispatchers.IO) {
        dao.getEventByLink(link)
    }
}
