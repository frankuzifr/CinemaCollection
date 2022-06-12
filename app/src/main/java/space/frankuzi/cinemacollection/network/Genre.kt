package space.frankuzi.cinemacollection.network

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("genre") val genre: String
)
