package space.frankuzi.cinemacollection.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import space.frankuzi.cinemacollection.data.room.dao.FavouriteFilmsDao
import space.frankuzi.cinemacollection.data.room.dao.FilmsDao
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity

@Database(
    version = 1,
    entities = [
        FilmDbEntity::class,
        FavouriteFilmDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFilmsDao(): FilmsDao

    abstract fun getFavouritesDao(): FavouriteFilmsDao
}