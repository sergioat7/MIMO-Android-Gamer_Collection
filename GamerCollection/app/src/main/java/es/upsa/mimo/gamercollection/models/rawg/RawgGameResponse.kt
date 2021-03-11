package es.upsa.mimo.gamercollection.models.rawg

import java.util.*

data class RawgGameResponse(
    val id: Int,
    val name: String?,
    val released: Date?,
    val backgroundImage: String?,
    val developers: List<RawgDeveloperResponse>?,
    val publishers: List<RawgPublisherResponse>?,
    val esrbRating: RawgEsrbResponse?
)