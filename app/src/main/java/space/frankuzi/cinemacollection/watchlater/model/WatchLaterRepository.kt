package space.frankuzi.cinemacollection.watchlater.model

import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.WatchLaterFilmDbEntity
import space.frankuzi.cinemacollection.watchlater.datetime.DateTime
import java.sql.Date

class WatchLaterRepository(
    private val database: AppDatabase
) {
    suspend fun addFilmToWatchLater(filmItem: FilmItem, dateTime: DateTime) {
        database.getWatchLaterDao().addWatchLaterFilm(WatchLaterFilmDbEntity(
            id = 0,
            kinopoiskId = filmItem.id,
            nameOriginal = filmItem.nameOriginal,
            nameRussian = filmItem.nameRu,
            description = filmItem.description,
            type = filmItem.type,
            imageUrl = filmItem.imageUrl,
            watchLaterDate = Date(dateTime.getDate().time)
        ))
    }

    suspend fun removeFilmFromWatchLater(filmItem: FilmItem) {
        database.getWatchLaterDao().removeWatchLaterFilm(filmItem.id)
    }

    suspend fun getWatchLaterFilms(): List<FilmItem>? {
        val watchLaterFilmsEntities = database.getWatchLaterDao().getWatchLaterFilms()

        val films = watchLaterFilmsEntities?.map {
            val filmItem = it.toFilmItem()
            filmItem
        }

        return films
    }

    suspend fun changeDate(filmItem: FilmItem, dateTime: DateTime) {
        database.getWatchLaterDao().changeDate(filmItem.id, Date(dateTime.getDate().time))
    }

    suspend fun getFilmById(kinopoiskId: Int): FilmItem? {
        val watchLaterFilmById = database.getWatchLaterDao().getWatchLaterFilmById(kinopoiskId)
        return watchLaterFilmById?.toFilmItem()
    }
}