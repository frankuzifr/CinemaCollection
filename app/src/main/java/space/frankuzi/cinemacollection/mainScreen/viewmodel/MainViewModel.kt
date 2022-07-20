package space.frankuzi.cinemacollection.mainScreen.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.mainScreen.model.LoadFilmsCallback
import space.frankuzi.cinemacollection.mainScreen.model.MainRepository
import space.frankuzi.cinemacollection.structs.LoadingError
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent

class MainViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase
    ) : ViewModel() {

    private var _isLoading = false

    private val _mainRepository = MainRepository(api, database)
    private val _favouriteRepository = FavouriteRepository(database)

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = SingleLiveEvent<Int>()
    private val _isLastFilmsPages = MutableLiveData<Boolean>()
    private val _loadError = SingleLiveEvent<LoadingError>()
    private val _refreshError = SingleLiveEvent<LoadingError>()
    private val _isRefreshing = MutableLiveData<Boolean>()

    val films: LiveData<List<FilmItem>> = _films
    val filmItemChanged: LiveData<Int> = _filmItemChanged
    val isLastFilmsPages: LiveData<Boolean> = _isLastFilmsPages
    val loadError: LiveData<LoadingError> = _loadError
    val refreshError: LiveData<LoadingError> = _refreshError
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadFilms() {
        if (_isLoading)
            return

        if (films.value != null)
            return

        _isLoading = true

        viewModelScope.launch(job) {
            _mainRepository.loadFilms(object : LoadFilmsCallback {
                override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {

                    _films.value = films

                    _isLoading = false

                    _isLastFilmsPages.value = isLastPages
                }

                override fun onError(error: LoadingError) {
                    _loadError.value = error
                    _isLoading = false
                }
            })
        }
    }

    fun refreshFilms() {
        if (_isLoading)
            _mainRepository.cancelLoad()

        _isLoading = true
        _isRefreshing.value = true

        viewModelScope.launch(job) {

            _mainRepository.loadFirstPageFilms(object : LoadFilmsCallback {
                override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                    _films.value = films
                    _isLoading = false
                    _isRefreshing.value = false

                    _isLastFilmsPages.value = isLastPages
                }

                override fun onError(error: LoadingError) {
                    _refreshError.value = error
                    _isLoading = false
                    _isRefreshing.value = false
                }
            })
        }
    }

    fun updateFilm(film: FilmItem) {
        val films = films.value

        val index = films?.indexOfFirst {
            it.id == film.id
        }

        index?.let {
            _filmItemChanged.value = it
        }
    }

    private fun addToFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            _favouriteRepository.addFilmToFavourite(filmItem)
        }
    }

    private fun removeFromFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            _favouriteRepository.removeFilmFromFavourite(filmItem)
        }
    }

    fun loadNextPage() {
        if (_isLoading)
            return

        _isLoading = true

        _mainRepository.loadNextPageFilms(object : LoadFilmsCallback {
            override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                _films.value = films

                _isLoading = false

                _isLastFilmsPages.value = isLastPages
            }

            override fun onError(error: LoadingError) {
                _loadError.value = error
                _isLoading = false
            }
        })
    }

    fun onClickFavourite(film: FilmItem) {
        if (film.isFavourite)
            removeFromFavourite(film)
         else
            addToFavourite(film)

        film.isFavourite = !film.isFavourite
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("", "cleared")
        job.cancel()
    }
}