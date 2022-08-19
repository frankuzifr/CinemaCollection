package space.frankuzi.cinemacollection.mainScreen.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.HttpException
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.network.response.GetFilmsResponse
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent

class MainRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase,
    private val context: Context
) {
    private val _films = MutableLiveData<List<FilmItem>>()
    private val _error = SingleLiveEvent<String>()
    private val _isLastPages = MutableLiveData<Boolean>()
    private val _isRefreshing = MutableLiveData<Boolean>()

    val films: LiveData<List<FilmItem>> = _films
    val error: LiveData<String> = _error
    val isLastPages: LiveData<Boolean> = _isLastPages
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var _currentPage = 1
    private var _lastPage = 20
    private var _loadFilmsCallback: Call<GetFilmsResponse>? = null

    fun loadNextPageFilms() {
        _currentPage++

        loadFilmsByApi()
    }

    fun loadFirstPageFilms() {
        _currentPage = 1

        loadFilmsByApi(true)
        _isRefreshing.value = true
    }

    fun loadFilms() {
        database.getFilmsDao().getFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<FilmDbEntity>?>{
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(films: List<FilmDbEntity>) {
                    if (films.isNotEmpty()) {
                        setPagesInfo(films.size)
                        getFilmsFromDatabase()

                    } else {
                        loadFirstPageFilms()
                    }
                }

                override fun onError(e: Throwable) {
                    loadFirstPageFilms()
                }
            })
    }

    fun searchFilmsByName(name: String) {
        _isLastPages.value = true

        database.getFilmsDao().findFilms("${name.lowercase().trim()}%")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map { film ->
                    film.toFilmItem()
                }
            }
            .subscribe(object : SingleObserver<List<FilmItem>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(films: List<FilmItem>) {
                    checkFilmsInFavourites(films)
                }

                override fun onError(e: Throwable) {
                    _films.value = emptyList()
                }
            })
    }

    private fun loadFilmsByApi(isRefreshing: Boolean = false) {
        filmsApi.getFilms(pageNumber = _currentPage)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                _lastPage = response.totalPages
                response.items.map {
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
            }
            .subscribe(
                object : SingleObserver<List<FilmDbEntity>>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(items: List<FilmDbEntity>) {

                        if (isRefreshing) {
                            addFilmsToDatabaseWithClear(items)
                            _isRefreshing.value = false
                        }
                        else {
                            addFilmsToDatabase(items)
                        }

                        getFilmsFromDatabase()
                    }

                    override fun onError(e: Throwable) {
                        when (e) {
                            is HttpException -> {
                                val errorText = context.getString(R.string.error_code, e.code().toString())
                                _error.value = errorText
                            }
                            else -> {
                                val errorText = context.getString(R.string.network_error)
                                _error.value = errorText
                            }
                        }

                        _currentPage--
                    }
                }
            )
    }

    private fun getFilmsFromDatabase(){

        database.getFilmsDao().getFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map { film ->
                    film.toFilmItem()
                }
            }
            .subscribe(
                object : SingleObserver<List<FilmItem>?>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(filmItems: List<FilmItem>) {
                        checkFilmsInFavourites(filmItems)
                    }

                    override fun onError(e: Throwable) {
                        _films.value = emptyList()
                    }
                }
            )
    }

    private fun checkFilmsInFavourites(films: List<FilmItem>) {
        runBlocking {
            launch {
                films.forEach { film ->

                    val favouriteFilm = database.getFavouritesDao().getFavouriteFilmById(film.id)

                    film.isFavourite = favouriteFilm != null
                }

                _films.value = films
            }
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
        checkIsLastPage()
    }

    fun checkIsLastPage() {
        _isLastPages.value = _currentPage == _lastPage
    }

    fun cancelLoad() {
        _loadFilmsCallback?.cancel()
    }
}