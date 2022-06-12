package space.frankuzi.cinemacollection.network.response

import com.google.gson.annotations.SerializedName
import space.frankuzi.cinemacollection.network.FilmItemResponse

data class GetFilmsResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("items") val items: List<FilmItemResponse?>
)