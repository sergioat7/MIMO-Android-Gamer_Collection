package es.upsa.mimo.gamercollection.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val games: List<GameResponse>,
    val sagas: List<SagaResponse>,
)
