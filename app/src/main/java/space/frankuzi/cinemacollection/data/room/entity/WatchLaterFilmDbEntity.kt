package space.frankuzi.cinemacollection.data.room.entity

import androidx.room.*
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.utils.DateConverter
import java.sql.Date

@Entity(tableName = "watch_later_films")
data class WatchLaterFilmDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "kinopoisk_id") val kinopoiskId: Int,
    @ColumnInfo(name = "name_original") val nameOriginal: String?,
    @ColumnInfo(name = "name_russian") val nameRussian: String?,
    val description: String?,
    val type: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @TypeConverters(DateConverter::class)
    @ColumnInfo(name = "watch_later_date") val watchLaterDate: Date?,
    ) {
    fun toFilmItem(): FilmItem {

        return FilmItem(
            id = kinopoiskId,
            nameRu = nameRussian,
            nameOriginal = nameOriginal,
            description = description,
            imageUrl = imageUrl,
            type = type,
            date = watchLaterDate
        )
    }
}