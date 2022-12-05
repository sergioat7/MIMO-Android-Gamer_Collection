package es.upsa.mimo.gamercollection.models.rawg

import com.google.gson.annotations.SerializedName
import es.upsa.mimo.gamercollection.utils.Constants
import java.util.*

data class RawgGameResponse(
    val id: Int,
    val name: String?,
    val released: Date?,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val rating: Double,
    val developers: List<RawgDeveloperResponse>?,
    val publishers: List<RawgPublisherResponse>?,
    @SerializedName("esrb_rating")
    val esrbRating: RawgEsrbResponse?
) {

    fun getDevelopersAsString(): String? {

        val result = StringBuilder()
        developers?.let {
            for (developer in it) {
                result.append(developer.name)
                result.append(Constants.NEXT_VALUE_SEPARATOR)
            }
        }
        return if (result.isNotBlank()) {
            StringBuilder(
                result.substring(
                    0,
                    result.length - Constants.NEXT_VALUE_SEPARATOR.length
                )
            ).toString()
        } else {
            null
        }
    }

    fun getPublishersAsString(): String? {

        val result = StringBuilder()
        publishers?.let {
            for (publisher in it) {
                result.append(publisher.name)
                result.append(Constants.NEXT_VALUE_SEPARATOR)
            }
        }
        return if (result.isNotBlank()) {
            StringBuilder(
                result.substring(
                    0,
                    result.length - Constants.NEXT_VALUE_SEPARATOR.length
                )
            ).toString()
        } else {
            null
        }
    }

    fun getRating(): String? {

        return when (esrbRating?.slug) {
            "everyone" -> "+3"
            "everyone-10-plus" -> "+7"
            "teen" -> "+12"
            "mature" -> "+16"
            "adults-only" -> "+18"
            else -> null
        }
    }
}