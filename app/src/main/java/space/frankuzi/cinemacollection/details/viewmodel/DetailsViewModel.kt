package space.frankuzi.cinemacollection.details.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.details.repository.DetailRepository
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.utils.livedatavariations.ExtendedLiveData
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import space.frankuzi.cinemacollection.watchlater.datetime.DateTime
import space.frankuzi.cinemacollection.watchlater.model.WatchLaterRepository
import java.sql.Date
import javax.inject.Inject

class DetailsViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase,
    private val context: Context
) : ViewModel() {
    private val _detailRepository = DetailRepository(api, database, context)
    private val _favouriteRepository = FavouriteRepository(database)
    private val _watchLaterRepository = WatchLaterRepository(database)

    private val _selectedItem = ExtendedLiveData<FilmItem?>()
    private val _loadError = SingleLiveEvent<String>()
    private val _favouriteStateChanged = SingleLiveEvent<FilmItem>()
    private val _descriptionLoaded = SingleLiveEvent<FilmItem>()
    private val _watchLaterChanged = SingleLiveEvent<FilmItem>()

    val selectedItem: LiveData<FilmItem?> = _selectedItem
    val loadError: LiveData<String> = _detailRepository.error
    val favouriteStateChanged: LiveData<FilmItem> = _favouriteStateChanged
    val descriptionLoaded: LiveData<FilmItem> = _detailRepository.description
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
            val filmItem = _watchLaterRepository.getFilmById(item.id)
            filmItem?.date?.let {
                item.date = it
            }

            _selectedItem.setValue(item)
            loadDescription()
        }
    }

    fun setItemById(id: Int) {
        viewModelScope.launch(job) {
            val filmItem = _watchLaterRepository.getFilmById(id)

            _selectedItem.setValue(filmItem)
            loadDescription()
        }
    }

    fun loadDescription() {
        selectedItem.value?.let {
            if (it.description == null)
                _detailRepository.loadDescription(it.id)
            else
                _descriptionLoaded.value = it
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
        private val database: AppDatabase,
        private val context: Context
    ): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == DetailsViewModel::class.java) {

                DetailsViewModel(api, database, context) as T
            } else {
                throw ClassNotFoundException()
            }
        }

    }
}