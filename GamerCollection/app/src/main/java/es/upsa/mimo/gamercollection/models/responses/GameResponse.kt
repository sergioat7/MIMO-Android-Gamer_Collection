package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import es.upsa.mimo.gamercollection.base.BaseModel
import es.upsa.mimo.gamercollection.extensions.toString
import es.upsa.mimo.gamercollection.models.rawg.RawgGameResponse
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
import java.util.*

@Entity(tableName = "Game")
data class GameResponse(
    @PrimaryKey
    @SerializedName("id")
    override var id: Int,
    @SerializedName("name")
    var name: String?,
    @SerializedName("platform")
    var platform: String?,
    @SerializedName("score")
    var score: Double,
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
    var imageUrl: String?,
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
    var songs: MutableList<SongResponse>
) : BaseModel<Int> {

    constructor(rawgGame: RawgGameResponse) : this(
        rawgGame.id,
        rawgGame.name,
        null,
        rawgGame.rating * 2,
        rawgGame.getRating(),
        rawgGame.getPublishersAsString(),
        rawgGame.getDevelopersAsString(),
        null,
        rawgGame.released,
        false,
        null,
        null,
        null,
        null,
        null,
        0.0,
        rawgGame.backgroundImage,
        null,
        null,
        null,
        null,
        mutableListOf()
    )

    fun releaseDateAsHumanReadable(): String? {

        return releaseDate.toString(
            SharedPreferencesHelper.dateFormatToShow,
            SharedPreferencesHelper.language
        )
    }

    fun purchaseDateAsHumanReadable(): String? {

        return purchaseDate.toString(
            SharedPreferencesHelper.dateFormatToShow,
            SharedPreferencesHelper.language
        )
    }
}