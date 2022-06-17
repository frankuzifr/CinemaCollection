package space.frankuzi.cinemacollection.repository

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.FilmsApi
import space.frankuzi.cinemacollection.network.response.FilmDescriptionResponse
import space.frankuzi.cinemacollection.room.FilmsDao

class DetailRepository(
    private val filmsApi: FilmsApi,
    private val database: FilmsDao
) {
    fun loadDescription(id: Int, loadFilmDescriptionCallback: LoadFilmDescriptionCallback) {
        filmsApi.getFilmById(id)
            .enqueue(object : Callback<FilmDescriptionResponse>{
                override fun onResponse(
                    call: Call<FilmDescriptionResponse>,
                    response: Response<FilmDescriptionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            setDescriptionToDatabase(id, it.description)
                            loadFilmDescriptionCallback.onSuccess(it.description)
                        }
                    } else {
                        loadFilmDescriptionCallback.onError("Код ошибки: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<FilmDescriptionResponse>, t: Throwable) {
                    loadFilmDescriptionCallback.onError("Ошибка подключения...")
                }

            })
    }

    private fun setDescriptionToDatabase(id: Int, description: String?) = runBlocking {
        launch {
            database.setFilmDescription(id, description)
        }
    }
}

interface LoadFilmDescriptionCallback {
    fun onSuccess(description: String?)
    fun onError(message: String)
}