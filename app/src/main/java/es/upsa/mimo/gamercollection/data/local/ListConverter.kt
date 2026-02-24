package es.upsa.mimo.gamercollection.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.upsa.mimo.gamercollection.data.local.model.GameEntity
import es.upsa.mimo.gamercollection.data.local.model.SongEntity
import java.util.*

class ListConverter {

    //region Private properties
    private val gson = Gson()
    //endregion

    //region Public methods
    @TypeConverter
    fun stringToSongList(data: String?): List<SongEntity?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType =
            object : TypeToken<List<SongEntity?>?>() {}.type
        return gson.fromJson<List<SongEntity?>>(data, listType)
    }

    @TypeConverter
    fun songListToString(songs: List<SongEntity?>?): String? {
        return gson.toJson(songs)
    }

    @TypeConverter
    fun stringToGameList(data: String?): List<GameEntity?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType =
            object : TypeToken<List<GameEntity?>?>() {}.type
        return gson.fromJson<List<GameEntity?>>(data, listType)
    }

    @TypeConverter
    fun gameListToString(games: List<GameEntity?>?): String? {
        return gson.toJson(games)
    }
    //endregion
}