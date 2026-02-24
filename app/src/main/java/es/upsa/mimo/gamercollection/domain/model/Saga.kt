package es.upsa.mimo.gamercollection.domain.model

import es.upsa.mimo.gamercollection.models.base.BaseModel

data class Saga(
    override val id: Int,
    val name: String?,
    val games: List<Game>
) : BaseModel<Int>