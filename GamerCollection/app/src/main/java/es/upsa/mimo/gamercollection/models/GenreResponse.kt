package es.upsa.mimo.gamercollection.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "Genre")
data class GenreResponse(
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)