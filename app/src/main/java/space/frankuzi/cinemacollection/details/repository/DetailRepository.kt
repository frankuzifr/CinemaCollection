package space.frankuzi.cinemacollection.details.repository

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import java.sql.Date

class DetailRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {

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