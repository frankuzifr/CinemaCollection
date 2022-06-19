package space.frankuzi.cinemacollection.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import space.frankuzi.cinemacollection.data.FilmItem
import java.util.*

@Entity(tableName = "favourite_films")
data class FavouriteFilmDbEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "kinopoisk_id") val kinopoiskId: Int,
    @ColumnInfo(name = "name_original") val nameOriginal: String?,
    @ColumnInfo(name = "name_russian") val nameRussian: String?,
    val description: String?,
    val type: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String
) {
    fun toFilmItem(): FilmItem {
        val language = Locale.getDefault().language

        return FilmItem(
            id = kinopoiskId,
            name = if (language != "ru" && nameOriginal != null && nameOriginal != "") nameOriginal else nameRussian,
            description = description,
            imageUrl = imageUrl,
            isFavourite = true
        )
    }
}