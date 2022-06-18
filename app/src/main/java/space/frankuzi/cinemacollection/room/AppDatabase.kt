package space.frankuzi.cinemacollection.room

import androidx.room.Database
import androidx.room.RoomDatabase
import space.frankuzi.cinemacollection.room.entity.FavouriteFilmDbEntity
import space.frankuzi.cinemacollection.room.entity.FilmDbEntity

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