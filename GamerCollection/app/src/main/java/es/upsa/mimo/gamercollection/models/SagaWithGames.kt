package es.upsa.mimo.gamercollection.models

import androidx.room.Embedded
import androidx.room.Relation
import es.upsa.mimo.gamercollection.models.responses.GameResponse
import es.upsa.mimo.gamercollection.models.responses.SagaResponse

data class SagaWithGames(
    @Embedded
    val saga: SagaResponse,
    @Relation(
        parentColumn = "id",
        entityColumn = "saga_id"
    )
    val games: List<GameResponse>
) {
    fun transform(): SagaResponse {
        return SagaResponse(
            saga.id,
            saga.name,
            games
        )
    }
}