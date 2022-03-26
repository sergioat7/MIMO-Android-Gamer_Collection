package es.upsa.mimo.gamercollection.models.responses

import es.upsa.mimo.gamercollection.base.BaseModel

data class PlatformResponse(
    override val id: String,
    val name: String
) : BaseModel<String>