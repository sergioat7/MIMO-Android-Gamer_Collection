package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Saga")
data class SagaEntity(
    @PrimaryKey
    override var id: Int,
    val name: String?,
    val games: List<GameEntity>
) : BaseEntity<Int>