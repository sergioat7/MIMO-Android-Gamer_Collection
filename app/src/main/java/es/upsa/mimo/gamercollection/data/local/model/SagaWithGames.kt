package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class SagaWithGames(
    @Embedded
    val saga: SagaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "saga_id"
    )
    val games: List<GameEntity>
) {
    fun transform(): SagaEntity {
        return SagaEntity(
            saga.id,
            saga.name,
            games
        )
    }
}