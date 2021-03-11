package es.upsa.mimo.gamercollection.models.rawg

import com.google.gson.annotations.SerializedName
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
)