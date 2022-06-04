package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData

class FavouritesViewModel : ViewModel() {
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = MutableLiveData<FilmItem>()
    private val _itemRemoved = MutableLiveData<FilmItem>()
    private val _itemRemoveCanceled = MutableLiveData<Int>()

    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val filmItemChanged: LiveData<FilmItem> = _filmItemChanged
    val itemRemoved: LiveData<FilmItem> = _itemRemoved
    val itemRemoveCanceled: LiveData<Int> = _itemRemoveCanceled


    fun loadFavouritesFilms() {
        _favouritesFilms.value = FilmsData.favouriteFilms
    }

    fun onItemRemoveFromFavourite(filmItem: FilmItem) {
        filmItem.isFavourite = false
        FilmsData.favouriteFilms.remove(filmItem)
        _itemRemoved.value = filmItem
    }

    fun onItemRemoveCancel(filmItem: FilmItem, index: Int) {
        filmItem.isFavourite = true
        FilmsData.favouriteFilms.add(index, filmItem)
        _itemRemoveCanceled.value = index
    }

    fun onFilmItemChanged(filmItem: FilmItem) {
        _filmItemChanged.value = filmItem
    }
}