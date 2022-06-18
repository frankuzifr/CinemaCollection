package space.frankuzi.cinemacollection.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.room.AppDatabase
import space.frankuzi.cinemacollection.room.entity.FavouriteFilmDbEntity
import space.frankuzi.cinemacollection.utils.SingleLiveEvent

class FavouriteRepository(
    private val database: AppDatabase
) {

    private val _favouriteStateChanged = MutableLiveData<FilmItem>()

    suspend fun addFilmToFavourite(filmItem: FilmItem) {
        database.getFavouritesDao().addToFavourite(FavouriteFilmDbEntity(
            id = 0,
            kinopoiskId = filmItem.id
        ))

        Log.i("", "ADDDD")

        _favouriteStateChanged.value = filmItem
    }

    suspend fun removeFilmFromFavourite(filmItem: FilmItem) {
        database.getFavouritesDao().removeFromFavourite(filmItem.id)

        _favouriteStateChanged.value = filmItem
    }

    suspend fun getFavourites(): List<FilmItem>? {
        val filmDbEntities = database.getFavouritesDao().getFavourites()
        val films = filmDbEntities?.map {
            val filmDbEntity = database.getFilmsDao().getFilmById(it.kinopoiskId)
            val filmItem = filmDbEntity.toFilmItem()
            filmItem.isFavourite = true
            filmItem
        }

        return films
    }
}