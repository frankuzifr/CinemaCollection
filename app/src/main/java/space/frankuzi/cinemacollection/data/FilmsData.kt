package space.frankuzi.cinemacollection.data

import space.frankuzi.cinemacollection.R

object FilmsData {
    val films = listOf(
        FilmItem(
            nameIdRes = R.string.arcane_title,
            descriptionIdRes = R.string.arcane_description,
            imageIdRes = R.drawable.arcane
        ),
        FilmItem(
            nameIdRes = R.string.jentelmens_title,
            descriptionIdRes = R.string.jentelmens_description,
            imageIdRes = R.drawable.jentelmeni
        ),
        FilmItem(
            nameIdRes = R.string.titansAttack_title,
            descriptionIdRes = R.string.titansAttack_description,
            imageIdRes = R.drawable.titans_attack
        ),
        FilmItem(
            nameIdRes = R.string.zveropolis_title,
            descriptionIdRes = R.string.zveropolis_description,
            imageIdRes = R.drawable.zootopia
        ),
        FilmItem(
            nameIdRes = R.string.lacasa_de_paper_title,
            descriptionIdRes = R.string.lacasa_de_paper_description,
            imageIdRes = R.drawable.lacasa_de_paper
        ),
        FilmItem(
            nameIdRes = R.string.lock_stock_and_two_smoking_barrels_title,
            descriptionIdRes = R.string.lock_stock_and_two_smoking_barrels_description,
            imageIdRes = R.drawable.lock_stock_and_two_smoking_barrels
        ),
        FilmItem(
            nameIdRes = R.string.peaky_blinders_title,
            descriptionIdRes = R.string.peaky_blinders_description,
            imageIdRes = R.drawable.peaky_blinder
        )
    )

    val favouriteFilms = mutableListOf<FilmItem>()
}

