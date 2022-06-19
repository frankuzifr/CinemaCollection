package space.frankuzi.cinemacollection.data

import space.frankuzi.cinemacollection.R
import java.util.*

data class FilmItem(
    val id: Int,
    val nameRu: String?,
    val nameOriginal: String?,
    var description: String?,
    val imageUrl: String?,
    val type: String?,
    var isFavourite: Boolean = false,
    var isSelected: Boolean = false
){
    val name: String?
    get() {
        val language = Locale.getDefault().language

        return if (language != "ru" && nameOriginal != null && nameOriginal != "") nameOriginal else nameRu
    }

    val favouriteIconId: Int
        get() {
            return if (isFavourite)
                R.drawable.ic_baseline_favorite_24
            else
                R.drawable.ic_baseline_favorite_border_24
        }

    val favouriteLabelId: Int
        get() {
            return if (isFavourite)
                R.string.no_liked
            else
                R.string.liked
        }

    val titleColorId: Int
    get() {
        return if (isSelected)
            R.color.purple
        else
            R.color.orange
    }
}
