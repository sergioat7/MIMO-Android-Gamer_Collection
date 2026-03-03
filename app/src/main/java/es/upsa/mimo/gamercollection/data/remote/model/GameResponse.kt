package es.upsa.mimo.gamercollection.data.remote.model

import java.util.Date
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameResponse(
    @SerialName("id")
    @Required
    override var id: Int,
    @SerialName("name")
    var name: String? = null,
    @SerialName("platform")
    var platform: String? = null,
    @SerialName("score")
    var score: Double,
    @SerialName("pegi")
    val pegi: String? = null,
    @SerialName("distributor")
    val distributor: String? = null,
    @SerialName("developer")
    val developer: String? = null,
    @SerialName("players")
    val players: String? = null,
    @SerialName("releaseDate")
    @Serializable(with = DateSerializer::class)
    val releaseDate: Date? = null,
    @SerialName("goty")
    val goty: Boolean,
    @SerialName("format")
    val format: String? = null,
    @SerialName("genre")
    val genre: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("purchaseDate")
    @Serializable(with = DateSerializer::class)
    val purchaseDate: Date? = null,
    @SerialName("purchaseLocation")
    val purchaseLocation: String? = null,
    @SerialName("price")
    val price: Double,
    @SerialName("imageUrl")
    var imageUrl: String? = null,
    @SerialName("videoUrl")
    val videoUrl: String? = null,
    @SerialName("loanedTo")
    val loanedTo: String? = null,
    @SerialName("observations")
    val observations: String? = null,
    @SerialName("saga")
    var saga: SagaResponse? = null,
    @SerialName("songs")
    var songs: List<SongResponse> = emptyList(),
) : BaseResponse<Int> {

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
        mutableListOf(),
    )
}