package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.upsa.mimo.gamercollection.base.BaseModel

@Entity(tableName = "Format")
data class FormatResponse(
    @PrimaryKey
    override val id: String,
    val name: String
) : BaseModel<String>