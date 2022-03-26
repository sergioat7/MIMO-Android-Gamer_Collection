package es.upsa.mimo.gamercollection.models.responses

import es.upsa.mimo.gamercollection.base.BaseModel

data class FormatResponse(
    override val id: String,
    val name: String
) : BaseModel<String>