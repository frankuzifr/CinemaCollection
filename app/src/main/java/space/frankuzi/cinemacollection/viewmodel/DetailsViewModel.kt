package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.network.FilmsApi
import space.frankuzi.cinemacollection.repository.DetailRepository
import space.frankuzi.cinemacollection.repository.LoadFilmDescriptionCallback
import space.frankuzi.cinemacollection.room.FilmsDao

class DetailsViewModel(
    private val api: FilmsApi,
    private val database: FilmsDao
) : ViewModel() {
    private val _selectedItem = MutableLiveData<FilmItem>()
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _favouriteToggleIsChanged = MutableLiveData<FilmItem>()
    private val _loadError = MutableLiveData<String>()

    val selectedItem: LiveData<FilmItem> = _selectedItem
    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val favouriteToggleIsChanged: LiveData<FilmItem> = _favouriteToggleIsChanged
    val loadError: LiveData<String> = _loadError

    private val detailRepository = DetailRepository(api, database)
    private lateinit var _selectedFilmItem: FilmItem

    fun setItem(item: FilmItem) {
        _selectedFilmItem = item
        item.isSelected = true

        loadDescription()
    }

    fun retryLoadDescription() {
        loadDescription()
    }

    fun loadDescription() {
        if (_selectedFilmItem.description == null) {
            detailRepository.loadDescription(_selectedFilmItem.id, object : LoadFilmDescriptionCallback{
                override fun onSuccess(description: String?) {
                    _selectedFilmItem.description = description
                    _selectedItem.value = _selectedFilmItem
                }

                override fun onError(message: String) {
                    _loadError.value = message
                }
            })
        } else {
            _selectedItem.value = _selectedFilmItem
        }
    }

    fun onClickFavourite(film: FilmItem) {
//        val filmName = resources.getString(film.nameIdRes)
        if (film.isFavourite) {
            film.isFavourite = false
            FilmsData.favouriteFilms.remove(film)
//            showToastWithText(requireActivity(), resources.getString(R.string.film_removed_from_favourites, filmName))
        } else {
            film.isFavourite = true
            FilmsData.favouriteFilms.add(film)
//            showToastWithText(requireActivity(), resources.getString(R.string.film_added_to_favourites, filmName))
        }

        _favouritesFilms.value = FilmsData.favouriteFilms
        onFavouriteToggleChanged(film.isFavourite)
    }

    fun onFavouriteToggleChanged(isFavourite: Boolean) {
        val selectedItem = _selectedItem.value

        selectedItem?.let {
            selectedItem.isFavourite = isFavourite

            if (isFavourite)
                FilmsData.favouriteFilms.add(it)
            else
                FilmsData.favouriteFilms.remove(it)

            _favouriteToggleIsChanged.value = it
        }
    }
}