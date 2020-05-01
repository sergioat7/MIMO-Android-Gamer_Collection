package es.upsa.mimo.gamercollection.persistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.upsa.mimo.gamercollection.models.SongResponse
import java.util.*

class SongListConverter {

    private val gson = Gson()

    @TypeConverter
    fun stringToSongList(data: String?): List<SongResponse?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType =
            object : TypeToken<List<SongResponse?>?>() {}.type
        return gson.fromJson<List<SongResponse?>>(data, listType)
    }

    @TypeConverter
    fun songListToString(someObjects: List<SongResponse?>?): String? {
        return gson.toJson(someObjects)
    }
}