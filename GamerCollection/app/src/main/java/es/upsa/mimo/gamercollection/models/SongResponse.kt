package es.upsa.mimo.gamercollection.models

import com.google.gson.annotations.SerializedName

class SongResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("singer")
    val singer: String,
    @SerializedName("url")
    val url: String
)