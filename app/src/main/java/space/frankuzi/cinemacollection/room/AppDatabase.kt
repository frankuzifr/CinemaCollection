package space.frankuzi.cinemacollection.room

import androidx.room.Database
import androidx.room.RoomDatabase
import space.frankuzi.cinemacollection.room.entity.FilmDbEntity

@Database(
    version = 1,
    entities = [
        FilmDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getFilmsDao(): FilmsDao
}