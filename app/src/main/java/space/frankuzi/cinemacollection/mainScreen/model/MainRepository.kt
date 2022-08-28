package space.frankuzi.cinemacollection.mainScreen.model

import android.util.Log
import kotlinx.coroutines.*
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity

class MainRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {

    private var _currentPage = 1
    private var _lastPage = 20

    private val myJob = Job()
//    private val mainRepositoryScope = CoroutineScope(Dispatchers.IO + myJob)

    suspend fun getNextPageFilms(): List<FilmItem> {
        _currentPage++

        loadFilmsByApi()
        return getFilmsFromDatabase()
    }

    suspend fun getFirstPageFilms(): List<FilmItem> {
        _currentPage = 1

        loadFilmsByApi(true)
        return getFilmsFromDatabase()
    }

    suspend fun getFilms(): List<FilmItem> {
        loadFilmsByApi()
        return getFilmsFromDatabase()
    }

    suspend fun loadFilms(): List<FilmItem> {
        val films = getFilmsFromDatabase()

        if (films.isNotEmpty())
            return films

        loadFilmsByApi()

        return getFilmsFromDatabase()
    }

//    fun loadFilms() {
//        database.getFilmsDao().getFilms()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : SingleObserver<List<FilmDbEntity>?>{
//                override fun onSubscribe(d: Disposable) = Unit
//
//                override fun onSuccess(films: List<FilmDbEntity>) {
//                    if (films.isNotEmpty()) {
//                        setPagesInfo(films.size)
//                        getFilmsFromDatabase()
//
//                    } else {
//                        loadFirstPageFilms()
//                    }
//                }
//
//                override fun onError(e: Throwable) {
//                    loadFirstPageFilms()
//                }
//            })
//    }

    suspend fun searchFilmsByName(name: String): List<FilmItem> {
        return database.getFilmsDao().findFilms("%${name.lowercase().trim()}%").map { filmDbEntity ->
            val filmItem = filmDbEntity.toFilmItem()
            filmItem.isFavourite = checkFilmsInFavourites(filmItem)
            filmItem
        }
    }

//    fun `2searchFilmsByName`(name: String) {
//        _isLastPages.value = true
//
//        database.getFilmsDao().findFilms("%${name.lowercase().trim()}%")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .map {
//                it.map { film ->
//                    film.toFilmItem()
//                }
//            }
//            .subscribe(object : SingleObserver<List<FilmItem>>{
//                override fun onSubscribe(d: Disposable) = Unit
//
//                override fun onSuccess(films: List<FilmItem>) {
//                    checkFilmsInFavourites(films)
//                }
//
//                override fun onError(e: Throwable) {
//                    _films.value = emptyList()
//                }
//            })
//    }

    suspend fun loadFilmsByApi(isRefreshing: Boolean = false) {
        val filmsResponse = filmsApi.getFilms(_currentPage)
        _lastPage = filmsResponse.totalPages
        val films = filmsResponse.items.map {
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
        Log.i("", "API")

        if (isRefreshing) {
            addFilmsToDatabaseWithClear(films)
        }
        else {
            addFilmsToDatabase(films)
        }
    }

//    private fun getLoadFilmsByApi(isRefreshing: Boolean = false) {
//        filmsApi.getFilms(pageNumber = _currentPage)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .map { response ->
//                _lastPage = response.totalPages
//                response.items.map {
//                    FilmDbEntity(
//                        id = 0,
//                        kinopoiskId = it.kinopoiskId,
//                        nameOriginal = it.nameOriginal,
//                        nameRussian = it.nameRu,
//                        description = null,
//                        type = null,
//                        imageUrl = it.posterUrl
//                    )
//                }
//            }
//            .subscribe(
//                object : SingleObserver<List<FilmDbEntity>>{
//                    override fun onSubscribe(d: Disposable) = Unit
//
//                    override fun onSuccess(items: List<FilmDbEntity>) {
//
//                        if (isRefreshing) {
//                            addFilmsToDatabaseWithClear(items)
//                            _isRefreshing.value = false
//                        }
//                        else {
//                            addFilmsToDatabase(items)
//                        }
//
//                        getFilmsFromDatabase()
//                    }
//
//                    override fun onError(e: Throwable) {
//                        when (e) {
//                            is HttpException -> {
//                                val errorText = context.getString(R.string.error_code, e.code().toString())
//                                _error.value = errorText
//                            }
//                            else -> {
//                                val errorText = context.getString(R.string.network_error)
//                                _error.value = errorText
//                            }
//                        }
//
//                        _currentPage--
//                    }
//                }
//            )
//    }

    suspend fun getFilmsFromDatabase(): List<FilmItem> {
        Log.i("", "DB")
        return database.getFilmsDao().getFilms().map { filmDbEntity ->
            val filmItem = filmDbEntity.toFilmItem()
            filmItem.isFavourite = checkFilmsInFavourites(filmItem)
            filmItem
        }
    }

//    private fun getFilmsFromDatabase(){
//
//        database.getFilmsDao().getFilms()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .map {
//                it.map { film ->
//                    film.toFilmItem()
//                }
//            }
//            .subscribe(
//                object : SingleObserver<List<FilmItem>?>{
//                    override fun onSubscribe(d: Disposable) = Unit
//
//                    override fun onSuccess(filmItems: List<FilmItem>) {
//                        checkFilmsInFavourites(filmItems)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        _films.value = emptyList()
//                    }
//                }
//            )
//    }

    private suspend fun checkFilmsInFavourites(film: FilmItem): Boolean {
        val favouriteFilm = database.getFavouritesDao().getFavouriteFilmById(film.id)

        return favouriteFilm != null
    }

    private suspend fun addFilmsToDatabase(filmsDbEntity: List<FilmDbEntity>) {
        database.getFilmsDao().addFilms(filmsDbEntity)
    }

    private suspend fun addFilmsToDatabaseWithClear(filmsDbEntity: List<FilmDbEntity>) {
        database.getFilmsDao().addFilmsWithClear(filmsDbEntity)
    }

    private fun setPagesInfo(filmsCount: Int) {
        _currentPage = filmsCount / 20
    }

    fun isLastPage(): Boolean {
        return _currentPage == _lastPage
    }
}