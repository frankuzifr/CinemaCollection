package space.frankuzi.cinemacollection.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.data.network.response.FilmWithDescriptionResponse
import space.frankuzi.cinemacollection.data.network.response.GetFilmsResponse

interface FilmsApi {
    @GET("films")
    fun getFilms(
        @Query("page") pageNumber: Int,
        @Query("ratingFrom") minRating: Int? = MainActivity.FIREBASE_CONFIG.minRate,
        @Query("ratingTo") maxRating: Int? = MainActivity.FIREBASE_CONFIG.minRate,
        @Query("yearFrom") minYear: Int? = MainActivity.FIREBASE_CONFIG.minYear,
        @Query("yearTo") maxYear: Int? = MainActivity.FIREBASE_CONFIG.maxYear
    ): Call<GetFilmsResponse>

    @GET("films/{id}")
    fun getFilmById(@Path("id") filmId: Int): Call<FilmWithDescriptionResponse>
}