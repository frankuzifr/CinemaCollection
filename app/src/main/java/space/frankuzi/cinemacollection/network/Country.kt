package space.frankuzi.cinemacollection.network

import com.google.gson.annotations.SerializedName

data class Country(
    @SerializedName("country") val country: String
)