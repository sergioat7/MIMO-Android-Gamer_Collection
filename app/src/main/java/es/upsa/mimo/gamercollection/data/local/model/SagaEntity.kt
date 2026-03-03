package es.upsa.mimo.gamercollection.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Saga")
data class SagaEntity(
    @SerialName("id")
    @PrimaryKey
    override var id: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("games")
    val games: List<GameEntity> = emptyList(),
) : BaseEntity<Int>