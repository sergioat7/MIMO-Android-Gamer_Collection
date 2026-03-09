package es.upsa.mimo.gamercollection.domain.model

import es.upsa.mimo.gamercollection.extensions.toString
import java.util.*

data class FilterModel(
    val platforms: List<String>,
    val genres: List<String>,
    val formats: List<String>,
    val minScore: Double,
    val maxScore: Double,
    val minReleaseDate: Date?,
    val maxReleaseDate: Date?,
    val minPurchaseDate: Date?,
    val maxPurchaseDate: Date?,
    val minPrice: Double,
    val maxPrice: Double,
    val isGoty: Boolean?,
    val isLoaned: Boolean?,
    val hasSaga: Boolean?,
    val hasSongs: Boolean?,
) {

    fun minReleaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        minReleaseDate.toString(dateFormat, language)

    fun maxReleaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        maxReleaseDate.toString(dateFormat, language)

    fun minPurchaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        minPurchaseDate.toString(dateFormat, language)

    fun maxPurchaseDateAsHumanReadable(dateFormat: String, language: String): String? =
        maxPurchaseDate.toString(dateFormat, language)
}