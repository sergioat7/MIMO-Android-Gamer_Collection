package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Song")
data class SongEntity(
    @PrimaryKey
    var id: Int,
    val name: String?,
    val singer: String?,
    val url: String?,
)