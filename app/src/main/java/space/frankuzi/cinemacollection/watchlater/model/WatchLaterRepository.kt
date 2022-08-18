package space.frankuzi.cinemacollection.watchlater.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.WatchLaterFilmDbEntity
import space.frankuzi.cinemacollection.watchlater.datetime.DateTime
import java.sql.Date

class WatchLaterRepository(
    private val database: AppDatabase
) {

    private val _films = MutableLiveData<List<FilmItem>>()

    val films: LiveData<List<FilmItem>> = _films

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

    fun getWatchLaterFilms() {
        database.getWatchLaterDao().getWatchLaterFilms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.map {film ->
                    film.toFilmItem()
                }
            }
            .subscribe(object : SingleObserver<List<FilmItem>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(items: List<FilmItem>) {
                    checkFilmsInFavourites(items)
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    private fun checkFilmsInFavourites(films: List<FilmItem>) {
        runBlocking {
            launch {
                films.forEach { film ->

                    val favouriteFilm = database.getFavouritesDao().getFavouriteFilmById(film.id)

                    film.isFavourite = favouriteFilm != null
                }

                _films.value = films
            }
        }
    }

    suspend fun changeDate(filmItem: FilmItem, dateTime: DateTime) {
        database.getWatchLaterDao().changeDate(filmItem.id, Date(dateTime.getDate().time))
    }

    suspend fun getFilmById(kinopoiskId: Int): FilmItem? {
        val watchLaterFilmById = database.getWatchLaterDao().getWatchLaterFilmById(kinopoiskId)
        return watchLaterFilmById?.toFilmItem()
    }
}