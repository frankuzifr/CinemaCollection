package space.frankuzi.cinemacollection.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single
import space.frankuzi.cinemacollection.data.room.entity.WatchLaterFilmDbEntity
import java.sql.Date

@Dao
interface WatchLaterDao {

    @Query("SELECT * FROM watch_later_films")
    fun getWatchLaterFilms(): Single<List<WatchLaterFilmDbEntity>?>

    @Query("SELECT * FROM watch_later_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun getWatchLaterFilmById(kinopoiskId: Int): WatchLaterFilmDbEntity?

    @Insert
    suspend fun addWatchLaterFilm(watchLaterFilmDbEntity: WatchLaterFilmDbEntity)

    @Query("DELETE FROM watch_later_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun removeWatchLaterFilm(kinopoiskId: Int)

    @Query("UPDATE watch_later_films SET watch_later_date = :newDate WHERE kinopoisk_id = :kinopoiskId")
    suspend fun changeDate(kinopoiskId: Int, newDate: Date)
}