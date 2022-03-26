package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.upsa.mimo.gamercollection.base.BaseModel

@Entity(tableName = "Genre")
data class GenreResponse(
    @PrimaryKey
    override val id: String,
    val name: String
) : BaseModel<String>