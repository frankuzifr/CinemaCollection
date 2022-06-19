package space.frankuzi.cinemacollection.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import space.frankuzi.cinemacollection.data.network.response.FilmDescriptionResponse
import space.frankuzi.cinemacollection.data.network.response.GetFilmsResponse

interface FilmsApi {
    @GET("films")
    fun getFilms(@Query("page") pageNumber: Int): Call<GetFilmsResponse>

    @GET("films/{id}")
    fun getFilmById(@Path("id") filmId: Int): Call<FilmDescriptionResponse>
}