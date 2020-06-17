package es.upsa.mimo.gamercollection.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import es.upsa.mimo.gamercollection.models.base.BaseModel

@Entity(tableName = "Genre")
data class GenreResponse(
    @PrimaryKey
    @SerializedName("id")
    override val id: String,
    @SerializedName("name")
    val name: String
): BaseModel<String>