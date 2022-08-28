package space.frankuzi.cinemacollection.details.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import java.sql.Date

class DetailRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {
    private val _description = SingleLiveEvent<FilmItem>()
    private val _item = MutableLiveData<FilmItem?>()
    private val _error = SingleLiveEvent<String>()

    val description: LiveData<FilmItem> = _description
    val item: LiveData<FilmItem?> = _item
    val error: LiveData<String> = _error

    suspend fun getDescriptionById(id: Int): String? {
        val filmById = filmsApi.getFilmById(id)
        setDescriptionToDatabase(id, filmById.description)
        return filmById.description
    }

    suspend fun getFilmById(id: Int): FilmItem {
        val filmById = filmsApi.getFilmById(id)
        val filmItem = FilmItem(
            id = id,
            nameRu = filmById.nameRu,
            nameOriginal = filmById.nameOriginal,
            description = filmById.description,
            imageUrl = filmById.posterUrl,
            type = null
        )

        filmItem.isFavourite = checkFilmsInFavourites(filmItem)
        filmItem.date = checkFilmInWatchLater(filmItem)

        return filmItem
    }

    private suspend fun checkFilmsInFavourites(film: FilmItem): Boolean {
        val favouriteFilm = database.getFavouritesDao().getFavouriteFilmById(film.id)

        return favouriteFilm != null
    }

    private suspend fun checkFilmInWatchLater(film: FilmItem): Date? {
        val watchLaterFilmById = database.getWatchLaterDao().getWatchLaterFilmById(film.id)

        return watchLaterFilmById?.watchLaterDate
    }

    private fun setDescriptionToDatabase(id: Int, description: String?) = runBlocking {
        launch {
            database.getFilmsDao().setFilmDescription(id, description)
        }
    }
}