package space.frankuzi.cinemacollection.data.network.response

import com.google.gson.annotations.SerializedName

data class FilmItemResponse(
    @SerializedName("kinopoiskId") val kinopoiskId: Int,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameOriginal") val nameOriginal: String?,
    @SerializedName("posterUrl") val posterUrl: String,
)
