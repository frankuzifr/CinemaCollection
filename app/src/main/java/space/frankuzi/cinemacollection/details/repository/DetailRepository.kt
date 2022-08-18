package space.frankuzi.cinemacollection.details.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent

class DetailRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase,
    private val context: Context
) {
    private val _description = SingleLiveEvent<FilmItem>()
    private val _item = MutableLiveData<FilmItem>()
    private val _error = SingleLiveEvent<String>()

    val description: LiveData<FilmItem> = _description
    val item: LiveData<FilmItem> = _item
    val error: LiveData<String> = _error

    fun loadDescription(id: Int){
        filmsApi.getFilmById(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                FilmItem(
                    id = id,
                    nameOriginal = it.nameOriginal,
                    nameRu = it.nameRu,
                    description = it.description,
                    imageUrl = it.posterUrl,
                    type = null
                )
            }
            .subscribe(
                object : SingleObserver<FilmItem> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(item: FilmItem) {
                        setDescriptionToDatabase(id, item.description)
                        _description.value = item
                    }

                    override fun onError(e: Throwable) {
                        when (e) {
                            is HttpException -> {
                                val errorText = context.getString(R.string.error_code, e.code().toString())
                                _error.value = errorText
                            }
                            else -> {
                                val errorText = context.getString(R.string.network_error)
                                _error.value = errorText
                            }
                        }
                    }
                }
            )
    }

    fun loadFilmById(kinopoiskId: Int) {
        filmsApi.getFilmById(kinopoiskId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                FilmItem(
                    id = kinopoiskId,
                    nameOriginal = it.nameOriginal,
                    nameRu = it.nameRu,
                    description = it.description,
                    imageUrl = it.posterUrl,
                    type = null
                )
            }
            .subscribe(object : SingleObserver<FilmItem> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(filmItem: FilmItem) {
                    checkFilmInFavourites(filmItem)
                }

                override fun onError(e: Throwable) {
                    when (e) {
                        is HttpException -> {
                            val errorText = context.getString(R.string.error_code, e.code().toString())
                            _error.value = errorText
                        }
                        else -> {
                            val errorText = context.getString(R.string.network_error)
                            _error.value = errorText
                        }
                    }
                }

            })
    }

    private fun checkFilmInFavourites(filmItem: FilmItem) = runBlocking {
        launch {
            val favouriteFilmById = database.getFavouritesDao().getFavouriteFilmById(filmItem.id)

            favouriteFilmById?.let {
                filmItem.isFavourite = true
            }

            checkFilmInWatchLater(filmItem)
        }
    }

    private fun checkFilmInWatchLater(filmItem: FilmItem) = runBlocking {
        launch {
            val watchLaterFilmById = database.getWatchLaterDao().getWatchLaterFilmById(filmItem.id)

            watchLaterFilmById?.let {
                filmItem.date = it.watchLaterDate
            }

            _item.value = filmItem
        }
    }

    private fun setDescriptionToDatabase(id: Int, description: String?) = runBlocking {
        launch {
            database.getFilmsDao().setFilmDescription(id, description)
        }
    }
}