package space.frankuzi.cinemacollection.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import space.frankuzi.cinemacollection.room.entity.FilmDbEntity

@Dao
interface FilmsDao {

    @Query("SELECT * FROM films")
    suspend fun getFilms(): List<FilmDbEntity>?

    @Insert
    suspend fun addFilms(filmDbEntities: List<FilmDbEntity>)

    @Insert
    suspend fun addFilm(filmDbEntity: FilmDbEntity)
}