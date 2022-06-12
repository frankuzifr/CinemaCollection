package space.frankuzi.cinemacollection.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse

class MainRepository {
    fun getFilms(getFilmsCallback: GetFilmsCallback) {
        val filmsApi = App.instance.filmsApi

        filmsApi.getFilms(1)
            .enqueue(object : Callback<GetFilmsResponse> {
                override fun onResponse(
                    call: Call<GetFilmsResponse>,
                    response: Response<GetFilmsResponse>
                ) {
                    if (response.isSuccessful) {

                        val getFilmsResponse = response.body()

                        val films = getFilmsResponse?.items?.map {
                            FilmItem(
                                name = it?.nameRu,
                                description = null,
                                imageUrl = it?.posterUrl
                            )
                        }

                        films?.let {
                            getFilmsCallback.onSuccess(it)
                        }
                    } else {
                        getFilmsCallback.onError("Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {
                    getFilmsCallback.onError("Network error probably...")
                }
            })
    }
}

interface GetFilmsCallback
{
    fun onSuccess(films: List<FilmItem>)
    fun onError(message: String)
}