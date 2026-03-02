package es.upsa.mimo.gamercollection.data.remote.model

data class FormatResponse(
    override val id: String,
    val name: String,
) : BaseResponse<String>

var FORMATS = listOf<FormatResponse>()