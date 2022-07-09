package space.frankuzi.cinemacollection.data.network.response

import com.google.gson.annotations.SerializedName

data class FilmWithDescriptionResponse(
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameOriginal") val nameOriginal: String?,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("description") val description: String?
    )