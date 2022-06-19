package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.FilmsApi
import space.frankuzi.cinemacollection.repository.DetailRepository
import space.frankuzi.cinemacollection.repository.FavouriteRepository
import space.frankuzi.cinemacollection.repository.LoadFilmDescriptionCallback
import space.frankuzi.cinemacollection.room.AppDatabase
import space.frankuzi.cinemacollection.utils.ExtendedLiveData
import space.frankuzi.cinemacollection.utils.SingleLiveEvent

class DetailsViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase
) : ViewModel() {
    private val _selectedItem = ExtendedLiveData<FilmItem?>()
    private val _loadError = SingleLiveEvent<String>()
    private val _filmChanged = SingleLiveEvent<FilmItem>()

    val selectedItem: LiveData<FilmItem?> = _selectedItem
    val loadError: LiveData<String> = _loadError
    val filmChanged: LiveData<FilmItem> = _filmChanged

    private val _detailRepository = DetailRepository(api, database.getFilmsDao())
    private val _favouriteRepository = FavouriteRepository(database)

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun setItem(item: FilmItem) {
        item.isSelected = true

        loadDescription(item)
    }

    private fun loadDescription(item: FilmItem) {
        item.let {
            if (it.description == null) {
                _detailRepository.loadDescription(it.id, object : LoadFilmDescriptionCallback{
                    override fun onSuccess(description: String?) {
                        it.description = description
                        _selectedItem.setValue(it)
                    }

                    override fun onError(message: String) {
                        _loadError.value = message
                    }
                })
            } else {
                _selectedItem.setValue(it)
            }
        }

    }

    fun onClickFavourite(film: FilmItem) {
        if (film.isFavourite)
            removeFromFavourite(film)
        else
            addToFavourite(film)

        film.isFavourite = !film.isFavourite
        _filmChanged.value = film
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
}