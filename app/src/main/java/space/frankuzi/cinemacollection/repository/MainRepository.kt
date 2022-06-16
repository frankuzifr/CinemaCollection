package space.frankuzi.cinemacollection.repository

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.FilmsApi
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse
import space.frankuzi.cinemacollection.room.FilmsDao
import space.frankuzi.cinemacollection.room.entity.FilmDbEntity

class MainRepository(
    private val filmsApi: FilmsApi,
    private val database: FilmsDao
) {
    private var _currentPage = 0
    private var _lastPage = 0

    fun loadNextPageFilms(loadFilmsCallback: LoadFilmsCallback) {
        _currentPage++
        Log.i("page", _currentPage.toString())

        loadFilmsByApi(loadFilmsCallback)
    }

    suspend fun refreshFilms(loadFilmsCallback: LoadFilmsCallback) {
        _currentPage = 1
        database.clearFilms()
        loadFilmsByApi(loadFilmsCallback)
    }

    suspend fun loadFilms(loadFilmsCallback: LoadFilmsCallback) {
        val films = database.getFilms()

        if (films != null && films.isNotEmpty()) {
            val filmItems = films.map { film ->
                film.toFilmItem()
            }

            setPagesInfo(filmItems.size)

            loadFilmsCallback.onSuccess(filmItems, _currentPage == _lastPage)
        } else {
            _currentPage = 1
            loadFilmsByApi(loadFilmsCallback)
        }
    }

    fun retryLoadCurrentPage(loadFilmsCallback: LoadFilmsCallback) {
        loadFilmsByApi(loadFilmsCallback)
    }

    private fun loadFilmsByApi(loadFilmsCallback: LoadFilmsCallback) {
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

                        val filmsDbEntity = mutableListOf<FilmDbEntity>()
                        getFilmsResponse?.items?.forEach {
                            filmsDbEntity.add(
                                FilmDbEntity(
                                    id = 0,
                                    kinopoiskId = it.kinopoiskId,
                                    nameOriginal = it.nameOriginal,
                                    nameRussian = it.nameRu,
                                    description = null,
                                    type = null,
                                    imageUrl = it.posterUrl
                                )
                            )
                        }

                        addFilmsToDatabase(filmsDbEntity)

                        getFilmsFromDatabase(loadFilmsCallback)
                    } else {
                        loadFilmsCallback.onError("Код ошибки: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {
                    loadFilmsCallback.onError("Ошибка подключения...")
                }
            })
    }

    private fun getFilmsFromDatabase(loadFilmsCallback: LoadFilmsCallback) = runBlocking {
        launch {
            var filmsEntities = database.getFilms()

            val films = filmsEntities?.map {
                it.toFilmItem()
            }

            if (films != null)
                loadFilmsCallback.onSuccess(films, _currentPage == _lastPage)
        }
    }

    private fun addFilmsToDatabase(filmsDbEntity: List<FilmDbEntity>) = runBlocking {
        launch {
            database.addFilms(filmsDbEntity)
        }
    }

    private fun setPagesInfo(filmsCount: Int) {
        filmsApi.getFilms(1)
            .enqueue(object : Callback<GetFilmsResponse>{
                override fun onResponse(
                    call: Call<GetFilmsResponse>,
                    response: Response<GetFilmsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val filmsResponse = response.body()
                        filmsResponse?.let {
                            _lastPage = filmsResponse.totalPages
                            _currentPage = filmsCount / filmsResponse.items.size
                        }
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {

                }
            })
    }
}

interface LoadFilmsCallback {
    fun onSuccess(films: List<FilmItem>, isLastPage: Boolean)
    fun onError(message: String)
}