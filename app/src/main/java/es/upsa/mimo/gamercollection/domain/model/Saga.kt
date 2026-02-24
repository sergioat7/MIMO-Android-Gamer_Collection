package es.upsa.mimo.gamercollection.domain.model

data class Saga(
    override val id: Int,
    val name: String?,
    val games: List<Game>
) : BaseModel<Int>