package es.upsa.mimo.gamercollection.persistence

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SongResponse
import java.util.*

class ListConverter {

    //region Private properties
    private val gson = Gson()
    //endregion

    //region Public methods
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
    fun songListToString(songs: List<SongResponse?>?): String? {
        return gson.toJson(songs)
    }

    @TypeConverter
    fun stringToGameList(data: String?): List<GameResponse?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType =
            object : TypeToken<List<GameResponse?>?>() {}.type
        return gson.fromJson<List<GameResponse?>>(data, listType)
    }

    @TypeConverter
    fun gameListToString(games: List<GameResponse?>?): String? {
        return gson.toJson(games)
    }
    //endregion
}