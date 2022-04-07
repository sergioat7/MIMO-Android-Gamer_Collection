package es.upsa.mimo.gamercollection.models

import es.upsa.mimo.gamercollection.extensions.toString
import es.upsa.mimo.gamercollection.utils.SharedPreferencesHelper
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
) {

    fun minReleaseDateAsHumanReadable(): String? {

        return minReleaseDate.toString(
            SharedPreferencesHelper.filterDateFormat,
            SharedPreferencesHelper.language
        )
    }

    fun maxReleaseDateAsHumanReadable(): String? {

        return maxReleaseDate.toString(
            SharedPreferencesHelper.filterDateFormat,
            SharedPreferencesHelper.language
        )
    }

    fun minPurchaseDateAsHumanReadable(): String? {

        return minPurchaseDate.toString(
            SharedPreferencesHelper.filterDateFormat,
            SharedPreferencesHelper.language
        )
    }

    fun maxPurchaseDateAsHumanReadable(): String? {

        return maxPurchaseDate.toString(
            SharedPreferencesHelper.filterDateFormat,
            SharedPreferencesHelper.language
        )
    }
}