package space.frankuzi.cinemacollection.watchlater.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.watchlater.model.WatchLaterRepository

class WatchLaterViewModel(
    private val database: AppDatabase
) : ViewModel() {

    private val watchLaterRepository = WatchLaterRepository(database)

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
}