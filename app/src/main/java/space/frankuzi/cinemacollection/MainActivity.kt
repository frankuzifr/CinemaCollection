package space.frankuzi.cinemacollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        for (film in FilmsData.films) {
            val filmCard = layoutInflater.inflate(R.layout.film_view_card, null)
            val filmImage = filmCard.findViewById<ImageView>(R.id.film_image)
            filmImage.setBackgroundResource(film.imageIdRes)

            val filmName = filmCard.findViewById<TextView>(R.id.film_title_label)
            filmName.setText(film.nameIdRes)

            val button = filmCard.findViewById<com.google.android.material.card.MaterialCardView>(R.id.film_card)

            button.setOnClickListener {
                val intent = Intent(this, FilmDetailActivity::class.java)
                intent.putExtra("imageIdRes", film.imageIdRes)
                intent.putExtra("nameIdRes", film.nameIdRes)
                intent.putExtra("descriptionIdRes", film.descriptionIdRes)
                intent.putExtra("isFavourite", film.isFavourite)
                startActivity(intent)
            }

            binding.container.addView(filmCard)
        }
    }
}