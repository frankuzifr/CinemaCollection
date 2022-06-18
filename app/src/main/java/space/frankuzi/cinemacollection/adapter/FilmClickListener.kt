package space.frankuzi.cinemacollection.adapter

import space.frankuzi.cinemacollection.data.FilmItem

interface FilmClickListener {
    fun onFilmClickListener(film: FilmItem, position: Int)
    fun onFilmFavouriteClickListener(film: FilmItem, position: Int)
}