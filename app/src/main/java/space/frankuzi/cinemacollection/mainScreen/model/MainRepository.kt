package space.frankuzi.cinemacollection.mainScreen.model

import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity

class MainRepository(
    private val filmsApi: FilmsApi,
    private val database: AppDatabase
) {

    private var _currentPage = 1
    private var _lastPage = 20

    suspend fun getNextPageFilms(): List<FilmItem> {
        _currentPage++

        return getFilms()
    }

    suspend fun getFirstPageFilms(): List<FilmItem> {
        _currentPage = 1

        loadFilmsByApi(true)
        return getFilmsFromDatabase()
    }

    suspend fun getFilms(): List<FilmItem> {
        loadFilmsByApi()
        return getFilmsFromDatabase()
    }

    suspend fun loadFilms(): List<FilmItem> {

        val films = getFilmsFromDatabase()

        if (films.isNotEmpty()) {
            setPagesInfo(films.size)
            return films
        }

        loadFilmsByApi()

        val filmsFromDatabase = getFilmsFromDatabase()

        return filmsFromDatabase
    }

    suspend fun searchFilmsByName(name: String): List<FilmItem> {
        return database.getFilmsDao().findFilms("%${name.lowercase().trim()}%").map { filmDbEntity ->
            val filmItem = filmDbEntity.toFilmItem()
            filmItem.isFavourite = checkFilmsInFavourites(filmItem)
            filmItem
        }
    }

    private suspend fun loadFilmsByApi(isRefreshing: Boolean = false) {
        val filmsResponse = filmsApi.getFilms(_currentPage)
        _lastPage = filmsResponse.totalPages
        val films = filmsResponse.items.map {
            FilmDbEntity(
                id = 0,
                kinopoiskId = it.kinopoiskId,
                nameOriginal = it.nameOriginal,
                nameRussian = it.nameRu,
                description = null,
                type = null,
                imageUrl = it.posterUrl
            )
        }

        if (isRefreshing) {
            addFilmsToDatabaseWithClear(films)
        }
        else {
            addFilmsToDatabase(films)
        }
    }

    private suspend fun getFilmsFromDatabase(): List<FilmItem> {
        println(database.toString())
        println(database.getFilmsDao())
        println(database.getFilmsDao().getFilms())
        return database.getFilmsDao().getFilms().map { filmDbEntity ->
            val filmItem = filmDbEntity.toFilmItem()
            filmItem.isFavourite = checkFilmsInFavourites(filmItem)
            filmItem
        }
    }

    private suspend fun checkFilmsInFavourites(film: FilmItem): Boolean {
        val favouriteFilm = database.getFavouritesDao().getFavouriteFilmById(film.id)

        return favouriteFilm != null
    }

    private suspend fun addFilmsToDatabase(filmsDbEntity: List<FilmDbEntity>) {
        database.getFilmsDao().addFilms(filmsDbEntity)
    }

    private suspend fun addFilmsToDatabaseWithClear(filmsDbEntity: List<FilmDbEntity>) {
        database.getFilmsDao().addFilmsWithClear(filmsDbEntity)
    }

    private fun setPagesInfo(filmsCount: Int) {
        _currentPage = filmsCount / 20
    }

    fun isLastPage(): Boolean {
        return _currentPage == _lastPage
    }
}