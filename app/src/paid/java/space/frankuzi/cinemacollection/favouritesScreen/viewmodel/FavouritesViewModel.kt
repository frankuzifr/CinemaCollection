package space.frankuzi.cinemacollection.favouritesScreen.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import javax.inject.Inject

class FavouritesViewModel(
    private val database: AppDatabase
) : ViewModel() {
    private val favouriteRepository = FavouriteRepository(database)

    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _itemRemoved = SingleLiveEvent<FilmItem>()
    private val _itemAdded = SingleLiveEvent<FilmItem>()
    private val _itemRemoveCanceled = SingleLiveEvent<FilmWithPosition>()

    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val itemRemoved: LiveData<FilmItem> = _itemRemoved
    val itemAdded: LiveData<FilmItem> = _itemAdded
    val itemRemoveCanceled: LiveData<FilmWithPosition> = _itemRemoveCanceled

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadFavouritesFilms() {
        viewModelScope.launch(job) {
            _favouritesFilms.value = favouriteRepository.getFavouritesFilms()
        }
    }

    fun onItemRemoveFromFavourite(filmItem: FilmItem) {
        viewModelScope.launch(job) {
            favouriteRepository.removeFilmFromFavourite(filmItem)
            _itemRemoved.value = filmItem
        }
    }

    fun changeFilmFavouriteState(filmItem: FilmItem) {
        if (filmItem.isFavourite) {
            _itemAdded.value = filmItem
        } else {
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

    class FavouritesViewModelFactory @Inject constructor(
        private val database: AppDatabase
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == FavouritesViewModel::class.java) {

                FavouritesViewModel(database) as T
            } else {
                throw ClassNotFoundException()
            }
        }

    }
}