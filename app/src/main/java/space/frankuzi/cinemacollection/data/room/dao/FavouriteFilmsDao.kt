package space.frankuzi.cinemacollection.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Single
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity

@Dao
interface FavouriteFilmsDao {

    @Query("SELECT * FROM favourite_films")
    fun getFavourites(): Single<List<FavouriteFilmDbEntity>?>

    @Query("SELECT * FROM favourite_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun getFavouriteFilmById(kinopoiskId: Int): FavouriteFilmDbEntity?

    @Insert
    suspend fun addToFavourite(favouriteFilmDbEntity: FavouriteFilmDbEntity)

    @Query("DELETE FROM favourite_films WHERE kinopoisk_id = :kinopoiskId")
    suspend fun removeFromFavourite(kinopoiskId: Int)
}