package es.upsa.mimo.gamercollection.models.responses

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.upsa.mimo.gamercollection.base.BaseModel

@Entity(tableName = "Platform")
data class PlatformResponse(
    @PrimaryKey
    override val id: String,
    val name: String
) : BaseModel<String>