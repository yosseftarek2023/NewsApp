package com.example.newsapp.dp

import androidx.room.TypeConverter
import com.example.newsapp.models.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromSource(source: Source?): String? {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(sourceString: String?): Source? {
        return if (sourceString.isNullOrEmpty()) null else {
            val sourceType = object : TypeToken<Source>() {}.type
            Gson().fromJson(sourceString, sourceType)
        }
    }
}
