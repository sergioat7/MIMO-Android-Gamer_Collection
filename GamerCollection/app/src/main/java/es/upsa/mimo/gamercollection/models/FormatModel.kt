package es.upsa.mimo.gamercollection.models

import es.upsa.mimo.gamercollection.base.BaseModel

data class FormatModel(
    override val id: String,
    val name: String
) : BaseModel<String>