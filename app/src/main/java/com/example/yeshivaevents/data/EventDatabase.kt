package com.example.yeshivaevents.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yeshivaevents.model.EventsResult

@Database(
    entities = [EventsResult::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance   // ✅ THIS was missing
                instance
            }
        }
    }
}
