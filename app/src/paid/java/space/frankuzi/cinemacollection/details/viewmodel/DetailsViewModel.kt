package space.frankuzi.cinemacollection.details.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.details.repository.DetailRepository
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.structs.FilmNote
import space.frankuzi.cinemacollection.details.repository.NotesRepository
import space.frankuzi.cinemacollection.favouritesScreen.model.FavouriteRepository
import space.frankuzi.cinemacollection.mainscreen.viewmodel.ErrorMessage
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
    private val _notesRepository = NotesRepository(database)

    private val _selectedItem = ExtendedLiveData<FilmItem?>()
    private val _loadError = SingleLiveEvent<ErrorMessage>()
    private val _favouriteStateChanged = SingleLiveEvent<FilmItem>()
    private val _descriptionLoaded = MutableLiveData<String?>()
    private val _watchLaterChanged = SingleLiveEvent<FilmItem>()
    private val _filmNotes = MutableLiveData<List<FilmNote>>()

    val selectedItem: LiveData<FilmItem?> = _selectedItem
    val loadError: LiveData<ErrorMessage> = _loadError
    val favouriteStateChanged: LiveData<FilmItem> = _favouriteStateChanged
    val descriptionLoaded: LiveData<String?> = _descriptionLoaded
    val watchLaterChanged: LiveData<FilmItem> = _watchLaterChanged
    val filmNotes: LiveData<List<FilmNote>> = _filmNotes

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

            loadNotes()
            loadDescription()
        }
    }

    fun setItemById(id: Int) {
        viewModelScope.launch(job) {
            val filmItem = _detailRepository.getFilmById(id)

            _selectedItem.setValue(filmItem)

            loadNotes()

            if (filmItem.description != null)
                _descriptionLoaded.value = filmItem.description
            else
                loadDescription()
        }
    }

    fun loadDescription() {
        selectedItem.value?.let {
            try {
                viewModelScope.launch {
                    _descriptionLoaded.value = _detailRepository.getDescriptionById(it.id)
                }
            } catch (httpException: HttpException) {
                _loadError.value = ErrorMessage(R.string.error_code, httpException.code().toString())
            } catch (throwable: Throwable) {
                _loadError.value = ErrorMessage(R.string.network_error)
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

    fun addFilmNote(note: FilmNote) {
        selectedItem.value?.let { filmItem ->
            viewModelScope.launch(job) {
                _notesRepository.addFilmNotes(filmItem.id, note)
            }
        }
    }

    private suspend fun loadNotes() {
        selectedItem.value?.let {
            val notes = _notesRepository.getFilmNotes(it.id)
            _filmNotes.value = notes
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