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

    private lateinit var _binding: ActivityMainBinding
    private val _requestCode: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        for ((index, film) in FilmsData.films.withIndex()) {
            val filmCard = layoutInflater.inflate(R.layout.film_view_card, null)
            val filmImage = filmCard.findViewById<ImageView>(R.id.film_image)
            filmImage.setBackgroundResource(film.imageIdRes)

            val filmName = filmCard.findViewById<TextView>(R.id.film_title_label)
            filmName.setText(film.nameIdRes)

            filmName.setTextColor(
                if (film.isSelected) resources.getColor(R.color.blue)
                else resources.getColor(R.color.orange)
            )

            val button = filmCard.findViewById<com.google.android.material.card.MaterialCardView>(R.id.film_card)

            button.setOnClickListener {
                val intent = Intent(this, FilmDetailActivity::class.java)
                intent.putExtra(FilmDetailActivity.FILM_ID, index)
                intent.putExtra(FilmDetailActivity.IMAGE_ID_RES, film.imageIdRes)
                intent.putExtra(FilmDetailActivity.NAME_ID_RES, film.nameIdRes)
                intent.putExtra(FilmDetailActivity.DESCRIPTION_ID_RES, film.descriptionIdRes)
                intent.putExtra(FilmDetailActivity.IS_FAVOURITE, film.isFavourite)
                film.isSelected = true
                filmName.setTextColor(resources.getColor(R.color.blue))
                startActivityForResult(intent, _requestCode)
            }

            _binding.container.addView(filmCard)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != _requestCode) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        if (resultCode != RESULT_OK)
            return

        if (data == null)
            return

        val filmId = data.getIntExtra(FilmDetailActivity.FILM_ID, 0)
        val isFavourite = data.getBooleanExtra(FilmDetailActivity.IS_FAVOURITE, false)
        val comment = data.getStringExtra(FilmDetailActivity.COMMENT)

        FilmsData.films[filmId].isFavourite = isFavourite

        Log.i("Film name", resources.getString(FilmsData.films[filmId].nameIdRes))
        Log.i("Favourite", isFavourite.toString())
        Log.i("Comment", comment.toString())
    }
}