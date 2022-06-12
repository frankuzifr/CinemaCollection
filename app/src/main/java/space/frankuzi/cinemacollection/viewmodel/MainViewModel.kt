package space.frankuzi.cinemacollection.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.repository.GetFilmsCallback
import space.frankuzi.cinemacollection.repository.MainRepository

class MainViewModel : ViewModel() {

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = MutableLiveData<FilmItem>()
    private val _mainRepository = MainRepository()

    val films: LiveData<List<FilmItem>> = _films
    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val filmItemChanged: LiveData<FilmItem> = _filmItemChanged

    fun loadFilms() {
        Log.i("ViewModel", "ViewModel")

        _mainRepository.getFilms(object : GetFilmsCallback{
            override fun onSuccess(films: List<FilmItem>) {
                _films.value = films
            }

            override fun onError(message: String) {
                Log.i("EROOOOR", message)
            }

        })

        //_films.value = FilmsData.films
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

    fun onFilmItemChanged(filmItem: FilmItem) {
        _filmItemChanged.value = filmItem
    }
}