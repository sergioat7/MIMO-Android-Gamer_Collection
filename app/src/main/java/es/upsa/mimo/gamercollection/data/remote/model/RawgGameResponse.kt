package es.upsa.mimo.gamercollection.data.remote.model

import java.util.Date
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val NEXT_VALUE_SEPARATOR = ", "

@Serializable
data class RawgGameResponse(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String? = null,
    @SerialName("released")
    @Serializable(with = DateSerializer::class)
    val released: Date? = null,
    @SerialName("background_image")
    val backgroundImage: String? = null,
    @SerialName("rating")
    val rating: Double = 0.0,
    @SerialName("developers")
    val developers: List<RawgDeveloperResponse>? = null,
    @SerialName("publishers")
    val publishers: List<RawgPublisherResponse>? = null,
    @SerialName("esrb_rating")
    val esrbRating: RawgEsrbResponse? = null,
) {

    fun getDevelopersAsString(): String? {
        val result = StringBuilder()
        developers?.let {
            for (developer in it) {
                result.append(developer.name)
                result.append(NEXT_VALUE_SEPARATOR)
            }
        }
        return if (result.isNotBlank()) {
            StringBuilder(
                result.substring(
                    0,
                    result.length - NEXT_VALUE_SEPARATOR.length,
                ),
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
                result.append(NEXT_VALUE_SEPARATOR)
            }
        }
        return if (result.isNotBlank()) {
            StringBuilder(
                result.substring(
                    0,
                    result.length - NEXT_VALUE_SEPARATOR.length,
                ),
            ).toString()
        } else {
            null
        }
    }

    fun getRating(): String? = when (esrbRating?.slug) {
        "everyone" -> "+3"
        "everyone-10-plus" -> "+7"
        "teen" -> "+12"
        "mature" -> "+16"
        "adults-only" -> "+18"
        else -> null
    }
}