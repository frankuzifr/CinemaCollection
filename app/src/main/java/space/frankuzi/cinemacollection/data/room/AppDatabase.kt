package space.frankuzi.cinemacollection.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import space.frankuzi.cinemacollection.data.room.dao.FavouriteFilmsDao
import space.frankuzi.cinemacollection.data.room.dao.FilmsDao
import space.frankuzi.cinemacollection.data.room.dao.NotesDao
import space.frankuzi.cinemacollection.data.room.dao.WatchLaterDao
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.data.room.entity.NotesDbEntity
import space.frankuzi.cinemacollection.data.room.entity.WatchLaterFilmDbEntity
import space.frankuzi.cinemacollection.utils.DateConverter

@Database(
    version = 1,
    entities = [
        FilmDbEntity::class,
        FavouriteFilmDbEntity::class,
        WatchLaterFilmDbEntity::class,
        NotesDbEntity::class
    ]
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFilmsDao(): FilmsDao

    abstract fun getFavouritesDao(): FavouriteFilmsDao

    abstract fun getWatchLaterDao(): WatchLaterDao

    abstract fun getNotesDao(): NotesDao
}