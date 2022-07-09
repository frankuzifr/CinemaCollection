package space.frankuzi.cinemacollection.details.repository

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.network.response.FilmWithDescriptionResponse
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.dao.FilmsDao
import space.frankuzi.cinemacollection.structs.ErrorType
import space.frankuzi.cinemacollection.structs.LoadingError

class DetailRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {
    fun loadDescription(id: Int, loadFilmDescriptionCallback: LoadFilmDescriptionCallback) {
        filmsApi.getFilmById(id)
            .enqueue(object : Callback<FilmWithDescriptionResponse>{
                override fun onResponse(
                    call: Call<FilmWithDescriptionResponse>,
                    response: Response<FilmWithDescriptionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            setDescriptionToDatabase(id, it.description)
                            loadFilmDescriptionCallback.onSuccess(it.description)
                        }
                    } else {
                        loadFilmDescriptionCallback.onError(LoadingError(ErrorType.RequestError, response.code()))
                    }
                }

                override fun onFailure(call: Call<FilmWithDescriptionResponse>, t: Throwable) {
                    loadFilmDescriptionCallback.onError(LoadingError(ErrorType.ConnectionError))
                }

            })
    }

    fun loadFilmById(kinopoiskId: Int, loadFilmCallback: LoadFilmByIdCallback) {
        filmsApi.getFilmById(kinopoiskId)
            .enqueue(object : Callback<FilmWithDescriptionResponse> {
                override fun onResponse(
                    call: Call<FilmWithDescriptionResponse>,
                    response: Response<FilmWithDescriptionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.let {
                            val filmItem = FilmItem(
                                id = kinopoiskId,
                                nameOriginal = it.nameOriginal,
                                nameRu = it.nameRu,
                                description = it.description,
                                imageUrl = it.posterUrl,
                                type = null
                            )

                            checkFilmInFavourites(filmItem, loadFilmCallback)
                        }
                    } else {
                        loadFilmCallback.onError(LoadingError(ErrorType.RequestError, response.code()))
                    }
                }

                override fun onFailure(call: Call<FilmWithDescriptionResponse>, t: Throwable) {
                    loadFilmCallback.onError(LoadingError(ErrorType.ConnectionError))
                }

            })
    }

    private fun checkFilmInFavourites(filmItem: FilmItem, loadFilmCallback: LoadFilmByIdCallback) = runBlocking {
        launch {
            val favouriteFilmById = database.getFavouritesDao().getFavouriteFilmById(filmItem.id)

            favouriteFilmById?.let {
                filmItem.isFavourite = true
            }

            checkFilmInWatchLater(filmItem, loadFilmCallback)
        }
    }

    private fun checkFilmInWatchLater(filmItem: FilmItem, loadFilmCallback: LoadFilmByIdCallback) = runBlocking {
        launch {
            val watchLaterFilmById = database.getWatchLaterDao().getWatchLaterFilmById(filmItem.id)

            watchLaterFilmById?.let {
                filmItem.date = it.watchLaterDate
            }

            loadFilmCallback.onSuccess(filmItem)
        }
    }

    private fun setDescriptionToDatabase(id: Int, description: String?) = runBlocking {
        launch {
            database.getFilmsDao().setFilmDescription(id, description)
        }
    }
}

interface LoadFilmDescriptionCallback {
    fun onSuccess(description: String?)
    fun onError(error: LoadingError)
}

interface LoadFilmByIdCallback {
    fun onSuccess(filmItem: FilmItem)
    fun onError(error: LoadingError)
}