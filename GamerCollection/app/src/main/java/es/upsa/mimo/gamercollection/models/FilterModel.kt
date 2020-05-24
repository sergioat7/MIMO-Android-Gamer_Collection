package es.upsa.mimo.gamercollection.models

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
    val isGoty: Boolean,
    val isLoaned: Boolean,
    val hasSaga: Boolean,
    val hasSongs: Boolean
)