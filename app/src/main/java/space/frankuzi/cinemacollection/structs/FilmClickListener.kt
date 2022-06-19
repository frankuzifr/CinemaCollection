package space.frankuzi.cinemacollection.structs

import space.frankuzi.cinemacollection.data.FilmItem

interface FilmClickListener {
    fun onFilmClickListener(film: FilmItem, position: Int)
    fun onFilmFavouriteClickListener(film: FilmItem, position: Int)
}