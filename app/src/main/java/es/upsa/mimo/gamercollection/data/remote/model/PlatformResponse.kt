package es.upsa.mimo.gamercollection.data.remote.model

data class PlatformResponse(
    override val id: String,
    val name: String,
) : BaseResponse<String>

var PLATFORMS = listOf<PlatformResponse>()