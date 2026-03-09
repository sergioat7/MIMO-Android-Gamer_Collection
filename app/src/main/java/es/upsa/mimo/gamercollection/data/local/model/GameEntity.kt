package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Game")
data class GameEntity(
    @SerialName("id")
    @PrimaryKey
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
    @Embedded(prefix = "saga_")
    var saga: SagaEntity? = null,
    @SerialName("songs")
    var songs: List<SongEntity> = emptyList(),
) : BaseEntity<Int>