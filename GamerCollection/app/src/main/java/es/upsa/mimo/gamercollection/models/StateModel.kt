package es.upsa.mimo.gamercollection.models

import es.upsa.mimo.gamercollection.models.base.BaseModel

data class StateModel(
    override val id: String,
    val name: String
) : BaseModel<String>