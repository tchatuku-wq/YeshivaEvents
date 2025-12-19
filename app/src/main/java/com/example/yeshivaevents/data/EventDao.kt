package com.example.yeshivaevents.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yeshivaevents.model.EventsResult

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    suspend fun getAllEvents(): List<EventsResult>

    @Query("SELECT * FROM events WHERE link = :link LIMIT 1")
    suspend fun getEventByLink(link: String): EventsResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventsResult>)
    @Query("DELETE FROM events")
    suspend fun clear()

}
