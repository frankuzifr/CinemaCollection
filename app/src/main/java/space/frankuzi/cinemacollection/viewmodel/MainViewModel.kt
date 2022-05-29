package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.showToastWithText

class MainViewModel : ViewModel() {

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()

    val films: LiveData<List<FilmItem>> = _films
    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms

    fun loadFilms() {
        _films.value = FilmsData.films
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
    }

    fun onClickFilmCard() {

    }
}