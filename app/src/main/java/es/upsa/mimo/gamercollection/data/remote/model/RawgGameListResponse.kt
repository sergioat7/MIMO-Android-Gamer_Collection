package es.upsa.mimo.gamercollection.data.remote.model

data class RawgGameListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<RawgGameResponse>
)