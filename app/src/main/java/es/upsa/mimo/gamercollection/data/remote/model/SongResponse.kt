package es.upsa.mimo.gamercollection.data.remote.model

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("singer")
    val singer: String?,
    @SerializedName("url")
    val url: String?
)