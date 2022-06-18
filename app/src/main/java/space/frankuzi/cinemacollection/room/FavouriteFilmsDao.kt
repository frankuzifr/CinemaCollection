package space.frankuzi.cinemacollection.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import space.frankuzi.cinemacollection.room.entity.FavouriteFilmDbEntity

@Dao
interface FavouriteFilmsDao {

    @Query("SELECT * FROM favourite_films")
    suspend fun getFavourites(): List<FavouriteFilmDbEntity>?

    @Query("SELECT * FROM favourite_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun getFavouriteFilmById(kinopoiskId: Int): FavouriteFilmDbEntity?

    @Insert
    suspend fun addToFavourite(favouriteFilmDbEntity: FavouriteFilmDbEntity)

    @Query("DELETE FROM favourite_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun removeFromFavourite(kinopoiskId: Int)
}