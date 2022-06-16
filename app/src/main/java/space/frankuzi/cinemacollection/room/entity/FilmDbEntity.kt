package space.frankuzi.cinemacollection.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.FilmItemResponse
import space.frankuzi.cinemacollection.network.response.FilmResponse
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse
import java.util.*

@Entity(tableName = "films")
data class FilmDbEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "name_original") val nameOriginal: String?,
    @ColumnInfo(name = "name_russian") val nameRussian: String?,
    val description: String?,
    val type: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String
) {
    fun toFilmItem(): FilmItem {
        val language = Locale.getDefault().language
        return FilmItem(
            id = id,
            name = if (language == "ru" && nameRussian != null) nameRussian else nameOriginal,
            description = description,
            imageUrl = imageUrl
        )
    }

    companion object {
        fun fromFilmResponse(filmResponse: FilmResponse): FilmDbEntity {
            return FilmDbEntity(
                id = filmResponse.kinopoiskId,
                nameOriginal = filmResponse.nameOriginal,
                nameRussian = filmResponse.nameRu,
                description = filmResponse.description,
                type = filmResponse.type,
                imageUrl = filmResponse.posterUrl
            )
        }
    }
}