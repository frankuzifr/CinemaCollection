package space.frankuzi.cinemacollection.data

import space.frankuzi.cinemacollection.R

object FilmsData {
    val films = listOf(
        Film(
            nameIdRes = R.string.arcane_title,
            descriptionIdRes = R.string.arcane_description,
            imageIdRes = R.drawable.arcane
        ),
        Film(
            nameIdRes = R.string.jentelmens_title,
            descriptionIdRes = R.string.jentelmens_description,
            imageIdRes = R.drawable.jentelmeni
        ),
        Film(
            nameIdRes = R.string.titansAttack_title,
            descriptionIdRes = R.string.titansAttack_description,
            imageIdRes = R.drawable.titans_attack
        ),
        Film(
            nameIdRes = R.string.zveropolis_title,
            descriptionIdRes = R.string.zveropolis_description,
            imageIdRes = R.drawable.zootopia
        ),
        Film(
            nameIdRes = R.string.lacasa_de_paper_title,
            descriptionIdRes = R.string.lacasa_de_paper_description,
            imageIdRes = R.drawable.lacasa_de_paper
        ),
        Film(
            nameIdRes = R.string.lock_stock_and_two_smoking_barrels_title,
            descriptionIdRes = R.string.lock_stock_and_two_smoking_barrels_description,
            imageIdRes = R.drawable.lock_stock_and_two_smoking_barrels
        ),
        Film(
            nameIdRes = R.string.peaky_blinders_title,
            descriptionIdRes = R.string.peaky_blinders_description,
            imageIdRes = R.drawable.peaky_blinder
        )
    )
}

