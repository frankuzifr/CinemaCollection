package space.frankuzi.cinemacollection.mainScreen.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.mainScreen.model.MainRepository
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import javax.inject.Inject

class MainViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase,
    private val context: Context
    ) : ViewModel() {

    private var _isLoading = false

    private val _mainRepository = MainRepository(api, database, context)
    private val _favouriteRepository = FavouriteRepository(database)

    private val _filmItemChanged = SingleLiveEvent<Int>()
    private val _refreshError = SingleLiveEvent<String>()
    private val _isRefreshing = MutableLiveData<Boolean>()

    val films: LiveData<List<FilmItem>> = _mainRepository.films
    val filmItemChanged: LiveData<Int> = _filmItemChanged
    val isLastFilmsPages: LiveData<Boolean> = _mainRepository.isLastPages
    val loadError: LiveData<String> = _mainRepository.error
    val refreshError: LiveData<String> = _refreshError
    val isRefreshing: LiveData<Boolean> = _mainRepository.isRefreshing

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
            _mainRepository.loadFilms()
            _isLoading = false
        }
    }

    fun refreshFilms() {
        if (_isLoading)
            _mainRepository.cancelLoad()

        _isLoading = true
        //_isRefreshing.value = true

        viewModelScope.launch(job) {

            _mainRepository.loadFirstPageFilms()
            _isLoading = false
           // _isRefreshing.value = false
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
        Log.i("", _isLoading.toString())
        if (_isLoading)
            return


        _isLoading = true

        viewModelScope.launch {
            _mainRepository.loadNextPageFilms()
            _isLoading = false
        }
    }

    fun onClickFavourite(film: FilmItem) {
        if (film.isFavourite)
            removeFromFavourite(film)
         else
            addToFavourite(film)

        film.isFavourite = !film.isFavourite
    }

    fun searchFilmsByName(name: String) {
        _mainRepository.searchFilmsByName(name)
    }

    fun checkIsLastPages() {
        _mainRepository.checkIsLastPage()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    class MainViewModelFactory @Inject constructor(
        private val api: FilmsApi,
        private val database: AppDatabase,
        private val context: Context
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == MainViewModel::class.java) {

                MainViewModel(api, database, context) as T
            } else {
                throw ClassNotFoundException()
            }
        }
    }
}