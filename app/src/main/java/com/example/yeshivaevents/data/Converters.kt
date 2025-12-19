package com.example.yeshivaevents.data

import androidx.room.TypeConverter
import com.example.yeshivaevents.model.EventDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.let { gson.toJson(it) }

    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json.isNullOrBlank()) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun fromEventDate(date: EventDate?): String? = date?.let { gson.toJson(it) }

    @TypeConverter
    fun toEventDate(json: String?): EventDate? {
        if (json.isNullOrBlank()) return null
        val type = object : TypeToken<EventDate>() {}.type
        return gson.fromJson(json, type)
    }
}
