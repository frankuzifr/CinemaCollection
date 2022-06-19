package space.frankuzi.cinemacollection.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity

@Dao
interface FilmsDao {

    @Transaction
    suspend fun addFilmsWithClear(filmDbEntities: List<FilmDbEntity>) {
        clearFilms()
        addFilms(filmDbEntities)
    }

    @Query("SELECT * FROM films")
    suspend fun getFilms(): List<FilmDbEntity>?

    @Insert
    suspend fun addFilms(filmDbEntities: List<FilmDbEntity>)

    @Insert
    suspend fun addFilm(filmDbEntity: FilmDbEntity)

    @Query("DELETE FROM films")
    suspend fun clearFilms()

    @Query("UPDATE films SET description = :description WHERE kinopoisk_id = :kinopoiskId")
    suspend fun setFilmDescription(kinopoiskId: Int, description: String?)

    @Query("SELECT * FROM films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun getFilmById(kinopoiskId: Int): FilmDbEntity
}