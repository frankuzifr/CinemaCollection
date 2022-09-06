package space.frankuzi.cinemacollection.details.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.details.repository.DetailRepository
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.utils.livedatavariations.ExtendedLiveData
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import space.frankuzi.cinemacollection.watchlater.datetime.DateTime
import space.frankuzi.cinemacollection.watchlater.model.WatchLaterRepository
import java.sql.Date
import javax.inject.Inject

class DetailsViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase
) : ViewModel() {
    private val _detailRepository = DetailRepository(api, database)
    private val _favouriteRepository = FavouriteRepository(database)
    private val _watchLaterRepository = WatchLaterRepository(database)

    private val _selectedItem = ExtendedLiveData<FilmItem?>()
    private val _loadError = SingleLiveEvent<String>()
    private val _favouriteStateChanged = SingleLiveEvent<FilmItem>()
    private val _descriptionLoaded = SingleLiveEvent<String?>()
    private val _watchLaterChanged = SingleLiveEvent<FilmItem>()

    val selectedItem: LiveData<FilmItem?> = _selectedItem
    val loadError: LiveData<String> = _detailRepository.error
    val favouriteStateChanged: LiveData<FilmItem> = _favouriteStateChanged
    val descriptionLoaded: LiveData<String?> = _descriptionLoaded
    val watchLaterChanged: LiveData<FilmItem> = _watchLaterChanged

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun setItem(item: FilmItem) {
        item.isSelected = true

        viewModelScope.launch(job) {
            val filmItem = _detailRepository.getFilmById(item.id)
            filmItem.date?.let {
                item.date = it
            }

            _selectedItem.setValue(item)
            loadDescription()
        }
    }

    fun setItemById(id: Int) {
        viewModelScope.launch(job) {
            val filmItem = _detailRepository.getFilmById(id)

            _selectedItem.setValue(filmItem)

            if (filmItem.description != null)
                _descriptionLoaded.value = filmItem.description
            else
                loadDescription()
        }
    }

    fun loadDescription() {
        selectedItem.value?.let {
            viewModelScope.launch {
                _descriptionLoaded.value = _detailRepository.getDescriptionById(it.id)
            }
        }
    }

    fun onClickFavourite(film: FilmItem) {
        if (film.isFavourite)
            removeFromFavourite(film)
        else
            addToFavourite(film)

        film.isFavourite = !film.isFavourite
        _favouriteStateChanged.value = film
    }

    fun setDateTime(dateTime: DateTime) {
        selectedItem.value?.let {
            viewModelScope.launch(job) {
                _watchLaterRepository.addFilmToWatchLater(it, dateTime)
                it.date = Date(dateTime.getDate().time)
                _watchLaterChanged.value = it
            }
        }
    }

    fun changeDateTime(dateTime: DateTime) {
        selectedItem.value?.let {
            viewModelScope.launch(job) {
                _watchLaterRepository.changeDate(it, dateTime)
                it.date = Date(dateTime.getDate().time)
                _watchLaterChanged.value = it
            }
        }
    }

    fun removeDateTime() {
        selectedItem.value?.let {
            viewModelScope.launch(job) {
                _watchLaterRepository.removeFilmFromWatchLater(it)
                it.date = null
                _watchLaterChanged.value = it
            }
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

    fun closeDetail() {
        _selectedItem.setValueWithoutNotify(null)
    }

    class DetailViewModelFactory @Inject constructor(
        private val api: FilmsApi,
        private val database: AppDatabase
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == DetailsViewModel::class.java) {

                DetailsViewModel(api, database) as T
            } else {
                throw ClassNotFoundException()
            }
        }

    }
}