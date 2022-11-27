package com.mvvm.newspaper.database.convertor

import androidx.room.TypeConverter
import com.mvvm.newspaper.model.Source

object Converters {

    @TypeConverter
    fun fromSource(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun toSource(name: String): Source {
        return Source(name, name)
    }
}