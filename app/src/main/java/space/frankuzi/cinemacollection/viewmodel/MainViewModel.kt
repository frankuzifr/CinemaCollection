package space.frankuzi.cinemacollection.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.FilmsApi
import space.frankuzi.cinemacollection.repository.FavouriteRepository
import space.frankuzi.cinemacollection.repository.LoadFilmsCallback
import space.frankuzi.cinemacollection.repository.MainRepository
import space.frankuzi.cinemacollection.room.AppDatabase
import space.frankuzi.cinemacollection.utils.SingleLiveEvent

class MainViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase
    ) : ViewModel() {

    private var _isLoading = false
    private val _mainRepository = MainRepository(api, database)
    private val _favouriteRepository = FavouriteRepository(database)

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = SingleLiveEvent<Int>()
    private val _isLastFilmsPages = SingleLiveEvent<Boolean>()
    private val _loadError = SingleLiveEvent<String>()

    val films: LiveData<List<FilmItem>> = _films
    val filmItemChanged: LiveData<Int> = _filmItemChanged
    val isLastFilmsPages: LiveData<Boolean> = _isLastFilmsPages
    val loadError: LiveData<String> = _loadError

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadFilms() {
        if (_isLoading)
            return

        _isLoading = true

        viewModelScope.launch(job) {
            _mainRepository.loadFilms(object : LoadFilmsCallback{
                override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {

                    _films.value = films

                    _isLoading = false

                    _isLastFilmsPages.value = isLastPages
                }

                override fun onError(message: String) {
                    _loadError.value = message
                    _isLoading = false
                }
            })
        }
    }

    fun refreshFilms() {

        viewModelScope.launch(job) {

            _mainRepository.refreshFilms(object : LoadFilmsCallback {
                override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                    _films.value = films
                    _isLoading = false

                    _isLastFilmsPages.value = isLastPages
                }

                override fun onError(message: String) {
                    _loadError.value = message
                    _isLoading = false
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

    fun addToFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            _favouriteRepository.addFilmToFavourite(filmItem)
        }
    }

    fun removeFromFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            _favouriteRepository.removeFilmFromFavourite(filmItem)
        }
    }

    fun loadNextPage() {
        if (_isLoading)
            return

        _isLoading = true

        _mainRepository.loadNextPageFilms(object : LoadFilmsCallback{
            override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                _films.value = films

                _isLoading = false

                _isLastFilmsPages.value = isLastPages
            }

            override fun onError(message: String) {
                _loadError.value = message
                _isLoading = false
            }
        })
    }

    fun retryLoadCurrentPage() {
        if (_isLoading)
            return

        _isLoading = true

        _mainRepository.retryLoadCurrentPage(object : LoadFilmsCallback{
            override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                _films.value = films
                _isLoading = false

                _isLastFilmsPages.value = isLastPages
            }

            override fun onError(message: String) {
                _loadError.value = message
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
        job.cancel()
    }
}