package space.frankuzi.cinemacollection.favouritesScreen.model

import androidx.lifecycle.MutableLiveData
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity

class FavouriteRepository(
    private val database: AppDatabase
) {

    suspend fun addFilmToFavourite(filmItem: FilmItem) {
        database.getFavouritesDao().addToFavourite(FavouriteFilmDbEntity(
            id = 0,
            kinopoiskId = filmItem.id,
            nameOriginal = filmItem.nameOriginal,
            nameRussian = filmItem.nameRu,
            description = filmItem.description,
            type = filmItem.type,
            imageUrl = filmItem.imageUrl
        ))
    }

    suspend fun removeFilmFromFavourite(filmItem: FilmItem) {
        database.getFavouritesDao().removeFromFavourite(filmItem.id)
    }

    suspend fun getFavourites(): List<FilmItem>? {
        val filmDbEntities = database.getFavouritesDao().getFavourites()
        val films = filmDbEntities?.map {
            val filmItem = it.toFilmItem()
            filmItem
        }

        return films
    }
}