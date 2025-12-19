package com.example.yeshivaevents.network

import com.example.yeshivaevents.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EventApiService {

    @GET("search.json")
    suspend fun getEvents(
        @Query("engine") engine: String = "google_events",
        @Query("q") query: String,
        @Query("hl") hl: String = "en",
        @Query("gl") gl: String = "us",
        @Query("api_key") apiKey: String
    ): EventResponse
}
