package es.upsa.mimo.gamercollection.models

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Game")
data class GameResponse(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("platform")
    val platform: String?,
    @SerializedName("score")
    val score: Double,
    @SerializedName("pegi")
    val pegi: String?,
    @SerializedName("distributor")
    val distributor: String?,
    @SerializedName("developer")
    val developer: String?,
    @SerializedName("players")
    val players: String?,
    @SerializedName("releaseDate")
    val releaseDate: String?,
    @SerializedName("goty")
    val goty: Boolean,
    @SerializedName("format")
    val format: String?,
    @SerializedName("genre")
    val genre: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("purchaseDate")
    val purchaseDate: String?,
    @SerializedName("purchaseLocation")
    val purchaseLocation: String?,
    @SerializedName("price")
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("videoUrl")
    val videoUrl: String?,
    @SerializedName("loanedTo")
    val loanedTo: String?,
    @SerializedName("observations")
    val observations: String?,
    @SerializedName("saga")
    val saga: SagaResponse,
    @SerializedName("songs")
    val songs: List<SongResponse>
)