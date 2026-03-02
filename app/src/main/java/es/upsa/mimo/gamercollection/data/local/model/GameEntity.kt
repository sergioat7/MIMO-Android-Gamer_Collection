package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Game")
data class GameEntity(
    @PrimaryKey
    override var id: Int,
    var name: String?,
    var platform: String?,
    var score: Double,
    val pegi: String?,
    val distributor: String?,
    val developer: String?,
    val players: String?,
    val releaseDate: Date?,
    val goty: Boolean,
    val format: String?,
    val genre: String?,
    val state: String?,
    val purchaseDate: Date?,
    val purchaseLocation: String?,
    val price: Double,
    var imageUrl: String?,
    val videoUrl: String?,
    val loanedTo: String?,
    val observations: String?,
    @Embedded(prefix = "saga_")
    var saga: SagaEntity?,
    var songs: MutableList<SongEntity>,
) : BaseEntity<Int>