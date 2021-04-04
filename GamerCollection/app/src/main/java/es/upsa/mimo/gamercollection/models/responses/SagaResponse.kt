package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import es.upsa.mimo.gamercollection.base.BaseModel

@Entity(tableName = "Saga")
data class SagaResponse(
    @PrimaryKey
    @SerializedName("id")
    override val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("games")
    val games: List<GameResponse>
) : BaseModel<Int>