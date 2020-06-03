package es.upsa.mimo.gamercollection.models

import androidx.room.*
import com.google.gson.annotations.SerializedName
import es.upsa.mimo.gamercollection.models.base.BaseModel
import java.util.*

@Entity(tableName = "Game")
data class GameResponse(
    @PrimaryKey
    @SerializedName("id")
    override val id: Int,
    @SerializedName("name")
    var name: String?,
    @SerializedName("platform")
    var platform: String?,
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
    val releaseDate: Date?,
    @SerializedName("goty")
    val goty: Boolean,
    @SerializedName("format")
    val format: String?,
    @SerializedName("genre")
    val genre: String?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("purchaseDate")
    val purchaseDate: Date?,
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
    @Embedded(prefix = "saga_")
    @SerializedName("saga")
    var saga: SagaResponse?,
    @SerializedName("songs")
    var songs: List<SongResponse>
): BaseModel<Int>