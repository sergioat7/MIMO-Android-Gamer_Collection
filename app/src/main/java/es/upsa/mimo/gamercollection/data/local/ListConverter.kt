package es.upsa.mimo.gamercollection.data.local

import androidx.room.TypeConverter
import es.upsa.mimo.gamercollection.data.local.model.GameEntity
import es.upsa.mimo.gamercollection.data.local.model.SongEntity
import kotlinx.serialization.json.Json

class ListConverter {

    @TypeConverter
    fun stringToSongList(data: String?): List<SongEntity> =
        data?.let { Json.decodeFromString<List<SongEntity>>(it) } ?: emptyList()

    @TypeConverter
    fun songListToString(songs: List<SongEntity>?): String? = songs?.let { Json.encodeToString(it) }

    @TypeConverter
    fun stringToGameList(data: String?): List<GameEntity> =
        data?.let { Json.decodeFromString<List<GameEntity>>(it) } ?: emptyList()

    @TypeConverter
    fun gameListToString(games: List<GameEntity>?): String? = games?.let { Json.encodeToString(it) }
}