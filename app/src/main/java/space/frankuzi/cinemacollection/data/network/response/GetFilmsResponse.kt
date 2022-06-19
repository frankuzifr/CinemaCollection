package space.frankuzi.cinemacollection.data.network.response

import com.google.gson.annotations.SerializedName

data class GetFilmsResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("items") val items: List<FilmItemResponse>
)