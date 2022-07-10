package space.frankuzi.cinemacollection.mainScreen.model

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.network.response.GetFilmsResponse
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.structs.ErrorType
import space.frankuzi.cinemacollection.structs.LoadingError

class MainRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {
    private var _currentPage = 1
    private var _lastPage = 20
    private var _loadFilmsCallback: Call<GetFilmsResponse>? = null

    fun loadNextPageFilms(loadFilmsCallback: LoadFilmsCallback) {
        _currentPage++

        loadFilmsByApi(loadFilmsCallback)
    }

    fun loadFirstPageFilms(loadFilmsCallback: LoadFilmsCallback) {
        _currentPage = 1

        loadFilmsByApi(loadFilmsCallback, true)
    }

    suspend fun loadFilms(loadFilmsCallback: LoadFilmsCallback) {
        val films = database.getFilmsDao().getFilms()

        if (films != null && films.isNotEmpty()) {
            setPagesInfo(films.size)
            getFilmsFromDatabase(loadFilmsCallback)

        } else {
            loadFirstPageFilms(loadFilmsCallback)
        }
    }


    private fun loadFilmsByApi(loadFilmsCallback: LoadFilmsCallback, isRefreshing: Boolean = false) {
        _loadFilmsCallback = filmsApi.getFilms(pageNumber = _currentPage)
        _loadFilmsCallback?.enqueue(object : Callback<GetFilmsResponse> {
                override fun onResponse(
                    call: Call<GetFilmsResponse>,
                    response: Response<GetFilmsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val getFilmsResponse = response.body()
                        getFilmsResponse?.let {
                            _lastPage = it.totalPages
                        }

                        val filmsDbEntity = getFilmsResponse?.items?.map {
                                FilmDbEntity(
                                    id = 0,
                                    kinopoiskId = it.kinopoiskId,
                                    nameOriginal = it.nameOriginal,
                                    nameRussian = it.nameRu,
                                    description = null,
                                    type = null,
                                    imageUrl = it.posterUrl
                                )
                        }

                        filmsDbEntity?.let {
                            if (isRefreshing)
                                addFilmsToDatabaseWithClear(filmsDbEntity)
                            else
                                addFilmsToDatabase(filmsDbEntity)

                            getFilmsFromDatabase(loadFilmsCallback)
                        }
                    } else {
                        loadFilmsCallback.onError(LoadingError(ErrorType.RequestError, response.code()))
                        _currentPage--
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {
                    if (call.isCanceled)
                        return

                    loadFilmsCallback.onError(LoadingError(ErrorType.ConnectionError))

                    _currentPage--
                }
            })
    }

    private fun getFilmsFromDatabase(loadFilmsCallback: LoadFilmsCallback) = runBlocking {
        launch {
            var filmsEntities = database.getFilmsDao().getFilms()

            val films = filmsEntities?.map {
                val favouriteFilmById =
                    database.getFavouritesDao().getFavouriteFilmById(it.kinopoiskId)

                val filmItem = it.toFilmItem()
                filmItem.isFavourite = favouriteFilmById != null

                filmItem
            }

            if (films != null)
                loadFilmsCallback.onSuccess(films, _currentPage == _lastPage)
        }
    }

    private fun addFilmsToDatabase(filmsDbEntity: List<FilmDbEntity>) = runBlocking {
        launch {
            database.getFilmsDao().addFilms(filmsDbEntity)
        }
    }

    private fun addFilmsToDatabaseWithClear(filmsDbEntity: List<FilmDbEntity>) = runBlocking {
        launch {
            database.getFilmsDao().addFilmsWithClear(filmsDbEntity)
        }
    }

    private fun setPagesInfo(filmsCount: Int) {
        _currentPage = filmsCount / 20
    }

    fun cancelLoad() {
        _loadFilmsCallback?.cancel()
    }
}

interface LoadFilmsCallback {
    fun onSuccess(films: List<FilmItem>, isLastPage: Boolean)
    fun onError(error: LoadingError)
}