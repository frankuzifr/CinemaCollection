package space.frankuzi.cinemacollection.repository

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse
import space.frankuzi.cinemacollection.room.entity.FilmDbEntity

class MainRepository {
    private var _currentPage = 1
    private var _lastPage = 0

    fun getNextPages(getFilmsCallback: GetFilmsCallback) {
        _currentPage++

        getFilmsByApi(getFilmsCallback)
    }

    suspend fun getFilms(getFilmsCallback: GetFilmsCallback) {
        val database = App.instance.database

        val films = database.getFilmsDao().getFilms()

        if (films != null && films.isNotEmpty()) {
            val filmItems = films.map { film ->
                film.toFilmItem()
            }

            Log.i("", "dfsdfsf")
            //todo
            getFilmsCallback.onSuccess(filmItems, false)
        } else {
            getFilmsByApi(getFilmsCallback)
        }
    }

    fun retryGetCurrentPage(getFilmsCallback: GetFilmsCallback) {
        getFilmsByApi(getFilmsCallback)
    }

    private fun getFilmsByApi(getFilmsCallback: GetFilmsCallback) {
        val filmsApi = App.instance.filmsApi

        filmsApi.getFilms(_currentPage)
            .enqueue(object : Callback<GetFilmsResponse> {
                override fun onResponse(
                    call: Call<GetFilmsResponse>,
                    response: Response<GetFilmsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val getFilmsResponse = response.body()
                        getFilmsResponse?.let {
                            _lastPage = it.totalPages
                        }

                        val films = mutableListOf<FilmItem>()
                        val filmsDbEntity = mutableListOf<FilmDbEntity>()
                        getFilmsResponse?.items?.forEach {
                            filmsDbEntity.add(
                                FilmDbEntity(
                                    id = it.kinopoiskId,
                                    nameOriginal = it.nameOriginal,
                                    nameRussian = it.nameRu,
                                    description = null,
                                    type = null,
                                    imageUrl = it.posterUrl
                                )
                            )

                            films.add(FilmItem(
                                id = it.kinopoiskId,
                                name = it.nameRu,
                                description = null,
                                imageUrl = it.posterUrl
                            ))
                        }

                        addFilmsToDb(filmsDbEntity)

                        films?.let {
                            getFilmsCallback.onSuccess(it, _currentPage == _lastPage)
                        }
                    } else {
                        getFilmsCallback.onError("Код ошибки: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {
                    getFilmsCallback.onError("Ошибка подключения...")
                }
            })
    }

    private fun addFilmsToDb(filmsDbEntity: List<FilmDbEntity>) = runBlocking {
        val database = App.instance.database

        launch {
            database.getFilmsDao().addFilms(filmsDbEntity)
        }
    }
}

interface GetFilmsCallback {
    fun onSuccess(films: List<FilmItem>, isLastPage: Boolean)
    fun onError(message: String)
}