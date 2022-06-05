package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData

class DetailsViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<FilmItem>()
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _favouriteToggleIsChanged = MutableLiveData<FilmItem>()

    val selectedItem: LiveData<FilmItem> = _selectedItem
    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val favouriteToggleIsChanged: LiveData<FilmItem> = _favouriteToggleIsChanged

    fun setItem(item: FilmItem) {
        item.isSelected = true
        _selectedItem.value = item
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