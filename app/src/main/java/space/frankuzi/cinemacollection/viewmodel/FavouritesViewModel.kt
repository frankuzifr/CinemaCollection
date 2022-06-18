package space.frankuzi.cinemacollection.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.repository.FavouriteRepository
import space.frankuzi.cinemacollection.room.AppDatabase
import space.frankuzi.cinemacollection.utils.SingleLiveEvent

class FavouritesViewModel(
    private val database: AppDatabase
) : ViewModel() {
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _itemRemoved = SingleLiveEvent<FilmItem>()
    private val _itemRemoveCanceled = SingleLiveEvent<FilmWithPosition>()

    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val itemRemoved: LiveData<FilmItem> = _itemRemoved
    val itemRemoveCanceled: LiveData<FilmWithPosition> = _itemRemoveCanceled

    private val favouriteRepository = FavouriteRepository(database)

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadFavouritesFilms() {
        Log.i("", "LOAAD")
        viewModelScope.launch(job) {
            _favouritesFilms.value = favouriteRepository.getFavourites()
        }
    }

    fun onItemRemoveFromFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            favouriteRepository.removeFilmFromFavourite(filmItem)
            _itemRemoved.value = filmItem
        }
    }

    fun onItemRemoveCancel(filmItem: FilmItem, index: Int) {
        viewModelScope.launch(job) {
            favouriteRepository.addFilmToFavourite(filmItem)
            _itemRemoveCanceled.value = FilmWithPosition(
                index,
                filmItem
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    inner class FilmWithPosition(
        val position: Int,
        val filmItem: FilmItem
    )
}