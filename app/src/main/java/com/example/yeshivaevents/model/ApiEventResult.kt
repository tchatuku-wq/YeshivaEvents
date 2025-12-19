package com.example.yeshivaevents.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("events_results")
    val events_results: List<EventsResult>?
)

data class EventDate(
    @SerializedName("start_date")
    val startDate: String?,

    @SerializedName("when")
    val whenText: String?
)

@Entity(tableName = "events")
data class EventsResult(
    @PrimaryKey
    @SerializedName("link")
    val link: String,

    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("date")
    val date: EventDate?,

    @SerializedName("address")
    val address: List<String>?,

    @SerializedName("thumbnail")
    val thumbnail: String?
)
