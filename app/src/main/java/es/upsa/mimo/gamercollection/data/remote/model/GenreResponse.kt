package es.upsa.mimo.gamercollection.data.remote.model

data class GenreResponse(
    override val id: String,
    val name: String
) : BaseResponse<String>

var GENRES = listOf<GenreResponse>()