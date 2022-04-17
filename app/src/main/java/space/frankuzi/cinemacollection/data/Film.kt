package space.frankuzi.cinemacollection.data

class Film(
    val nameIdRes: Int,
    val descriptionIdRes: Int,
    val imageIdRes: Int,
    var isFavourite: Boolean = false,
    var isSelected: Boolean = false
)