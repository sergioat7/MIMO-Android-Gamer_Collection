package es.upsa.mimo.gamercollection.models.rawg

data class RawgGameListResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<RawgGameResponse>
)