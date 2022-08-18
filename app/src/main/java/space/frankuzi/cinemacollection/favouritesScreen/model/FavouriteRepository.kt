package space.frankuzi.cinemacollection.favouritesScreen.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity

class FavouriteRepository(
    private val database: AppDatabase
) {

    private val _films = MutableLiveData<List<FilmItem>>()

    val films: LiveData<List<FilmItem>> = _films

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

    fun getFavourites() {
        database.getFavouritesDao().getFavourites()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map {film ->
                    film.toFilmItem()
                }
            }
            .subscribe(object : SingleObserver<List<FilmItem>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(items: List<FilmItem>) {
                    _films.value = items
                }

                override fun onError(e: Throwable) {

                }
            })
    }
}