package space.frankuzi.cinemacollection.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_films")
data class FavouriteFilmDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "kinopoisk_id") val kinopoiskId: Int
)