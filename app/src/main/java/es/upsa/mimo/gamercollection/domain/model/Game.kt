package es.upsa.mimo.gamercollection.domain.model

import es.upsa.mimo.gamercollection.extensions.toString
import java.util.Date

data class Game(
    override val id: Int,
    val name: String?,
    val platform: String?,
    val score: Double,
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
    val imageUrl: String?,
    val videoUrl: String?,
    val loanedTo: String?,
    val observations: String?,
    val saga: Saga?,
    val songs: List<Song>,
) : BaseModel<Int> {

    fun releaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        releaseDate.toString(dateFormat, language)

    fun purchaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        purchaseDate.toString(dateFormat, language)
}