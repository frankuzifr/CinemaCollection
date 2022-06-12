package space.frankuzi.cinemacollection.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse

interface FilmsApi {
    @GET("films")
    fun getFilms(@Query("page") pageNumber: Int): Call<GetFilmsResponse>
}