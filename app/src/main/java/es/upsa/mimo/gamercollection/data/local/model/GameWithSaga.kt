package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class GameWithSaga(
    @Embedded val game: GameEntity,
    @Relation(
        parentColumn = "saga_id",
        entityColumn = "id"
    )
    val saga: SagaEntity?
) {
    fun transform(): GameEntity {
        return GameEntity(
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
            game.songs
        )
    }
}