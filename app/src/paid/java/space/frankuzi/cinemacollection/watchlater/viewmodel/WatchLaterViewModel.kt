package space.frankuzi.cinemacollection.watchlater.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.watchlater.model.WatchLaterRepository
import javax.inject.Inject

class WatchLaterViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val watchLaterRepository = WatchLaterRepository(database)
    private val _favouriteRepository = FavouriteRepository(database)

    private var _watchLaterFilms = MutableLiveData<List<FilmItem>>()

    val watchLaterFilms: LiveData<List<FilmItem>> = _watchLaterFilms

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadWatchLaterFilms() {
        viewModelScope.launch(job) {
            _watchLaterFilms.value = watchLaterRepository.getWatchLaterFilms()
        }
    }

    fun onClickFavourite(film: FilmItem) {
        if (film.isFavourite)
            removeFromFavourite(film)
        else
            addToFavourite(film)

        film.isFavourite = !film.isFavourite
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

    class WatchLaterViewModelFactory @Inject constructor(
        private val database: AppDatabase
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == WatchLaterViewModel::class.java) {

                WatchLaterViewModel(database) as T
            } else {
                throw ClassNotFoundException()
            }
        }
    }
}