package space.frankuzi.cinemacollection.network

import com.google.gson.annotations.SerializedName

data class FilmItemResponse(
    @SerializedName("kinopoiskId") val kinopoiskId: Int,
//    @SerializedName("imdbId") val imdbId: String?,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameOriginal") val nameOriginal: String?,
//    @SerializedName("countries") val countries: List<Country>,
//    @SerializedName("genres") val genres: List<Genre>,
//    @SerializedName("ratingKinopoisk") val ratingKinopoisk: Float?,
//    @SerializedName("ratingImdb") val ratingImdb: Float?,
//    @SerializedName("year") val year: Float?,
//    @SerializedName("type") val type: String,
    @SerializedName("posterUrl") val posterUrl: String,
//    @SerializedName("posterUrlPreview") val posterUrlPreview: String?
)
