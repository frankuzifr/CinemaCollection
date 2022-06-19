package space.frankuzi.cinemacollection.data.network.response

import com.google.gson.annotations.SerializedName

data class FilmDescriptionResponse(
    @SerializedName("description") val description: String?
    )