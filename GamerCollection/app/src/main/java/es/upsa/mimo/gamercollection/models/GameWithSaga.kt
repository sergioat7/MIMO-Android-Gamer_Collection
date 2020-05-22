package es.upsa.mimo.gamercollection.models

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithSaga(
    @Embedded val game: GameResponse,
    @Relation(
        parentColumn = "saga_id",
        entityColumn = "id"
    )
    val saga: SagaResponse?
) {
    fun transform(): GameResponse {
        return GameResponse(
            game.id,
            game.name,
            game.platform,
            game.score,
            game.pegi,
            game.distributor,
            game.developer,
            game.players,
            game.releaseDate,
            game.goty,
            game.format,
            game.genre,
            game.state,
            game.purchaseDate,
            game.purchaseLocation,
            game.price,
            game.imageUrl,
            game.videoUrl,
            game.loanedTo,
            game.observations,
            saga,
            game.songs)
    }
}