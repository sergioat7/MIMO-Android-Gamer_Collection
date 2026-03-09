package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Song")
data class SongEntity(
    @SerialName("id")
    @PrimaryKey
    var id: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("singer")
    val singer: String? = null,
    @SerialName("url")
    val url: String? = null,
)