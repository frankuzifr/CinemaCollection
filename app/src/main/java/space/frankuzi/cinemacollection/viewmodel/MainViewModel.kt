package space.frankuzi.cinemacollection.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.repository.GetFilmsCallback
import space.frankuzi.cinemacollection.repository.MainRepository

class MainViewModel : ViewModel() {

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _favouritesFilms = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = MutableLiveData<FilmItem>()
    private val _mainRepository = MainRepository()
    private val _isLastFilmsPages = MutableLiveData<Boolean>()
    private val _loadError = MutableLiveData<String>()

    val films: LiveData<List<FilmItem>> = _films
    val favouritesFilms: LiveData<List<FilmItem>> = _favouritesFilms
    val filmItemChanged: LiveData<FilmItem> = _filmItemChanged
    val isLastFilmsPages: LiveData<Boolean> = _isLastFilmsPages
    val loadError: LiveData<String> = _loadError

    fun loadFilms() {

        _mainRepository.getNextPages(object : GetFilmsCallback{
            override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                if (_films.value == null)
                    _films.value = films
                else {
                    _films.value?.let {
                        val filmsValue = it.toMutableList()
                        filmsValue.addAll(films)
                        _films.value = filmsValue
                    }
                }

                _isLastFilmsPages.value = isLastPages
            }

            override fun onError(message: String) {
                _loadError.value = message
            }
        })

        //_films.value = FilmsData.films
    }

    fun retryLoadCurrentPage() {
        _mainRepository.retryGetCurrentPage(object : GetFilmsCallback{
            override fun onSuccess(films: List<FilmItem>, isLastPages: Boolean) {
                if (_films.value == null)
                    _films.value = films
                else {
                    _films.value?.let {
                        val filmsValue = it.toMutableList()
                        filmsValue.addAll(films)
                        _films.value = filmsValue
                    }
                }

                _isLastFilmsPages.value = isLastPages
            }

            override fun onError(message: String) {
                _loadError.value = message
                Log.i("EROOOOR", message)
            }
        })
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