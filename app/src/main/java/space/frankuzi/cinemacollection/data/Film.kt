package space.frankuzi.cinemacollection.data

import space.frankuzi.cinemacollection.R

data class Film(
    val nameIdRes: Int,
    val descriptionIdRes: Int,
    val imageIdRes: Int,
    var isFavourite: Boolean = false,
    var isSelected: Boolean = false
){
    val favouriteIconId: Int
        get() {
            return if (isFavourite)
                R.drawable.ic_baseline_favorite_24
            else
                R.drawable.ic_baseline_favorite_border_24
        }
}
