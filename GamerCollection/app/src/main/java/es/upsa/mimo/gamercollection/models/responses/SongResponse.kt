package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Song")
data class SongResponse(
    @PrimaryKey
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("singer")
    val singer: String?,
    @SerializedName("url")
    val url: String?
)