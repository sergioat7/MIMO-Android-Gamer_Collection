package es.upsa.mimo.gamercollection.data.remote.model

data class StateResponse(
    override val id: String,
    val name: String
) : BaseResponse<String>