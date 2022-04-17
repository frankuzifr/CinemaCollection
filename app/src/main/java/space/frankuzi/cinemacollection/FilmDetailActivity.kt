package space.frankuzi.cinemacollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import space.frankuzi.cinemacollection.databinding.ActivityFilmDetailBinding
import java.lang.Exception

class FilmDetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityFilmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFilmDetailBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        val filmId = intent.getIntExtra(FILM_ID, 0)
        val imageIdRes = intent.getIntExtra(IMAGE_ID_RES, 0)
        val nameIdRes = intent.getIntExtra(NAME_ID_RES, 0)
        val descriptionIdRes = intent.getIntExtra(DESCRIPTION_ID_RES, 0)
        var isFavourite = intent.getBooleanExtra(IS_FAVOURITE, false)

        _binding.filmImage.setBackgroundResource(imageIdRes)
        val detailToolbar = _binding.detailToolbar
        detailToolbar.setTitle(nameIdRes)
        _binding.filmDescription.setText(descriptionIdRes)

        detailToolbar.setNavigationOnClickListener {
            val intent = Intent()
            intent.putExtra(FILM_ID, filmId)
            intent.putExtra(IS_FAVOURITE, isFavourite)
            intent.putExtra(COMMENT, _binding.comment.editText?.text.toString())
            setResult(RESULT_OK, intent)
            finish()
        }

        val favouriteItem = detailToolbar.menu.getItem(0)
        favouriteItem.isChecked = isFavourite

        favouriteItem.setIcon(
            if (isFavourite) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )

        detailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> onShareButtonClick(resources.getString(nameIdRes))
                R.id.favourite -> {
                    if (it.isChecked){
                        it.isChecked = false
                        isFavourite = false
                        it.setIcon(R.drawable.ic_baseline_favorite_border_24)
                    } else {
                        it.isChecked = true
                        isFavourite = true
                        it.setIcon(R.drawable.ic_baseline_favorite_24)
                    }
                    Log.i("", it.isChecked.toString())
                }
                else -> throw Exception()
            }
            true
        }
    }

    private fun onShareButtonClick(filmName: String) {

        val sendMessageIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Привет. Давай посмотрим фильм $filmName")
        }

        val sharedIntent = Intent.createChooser(sendMessageIntent, null)

        startActivity(sharedIntent)
    }

    companion object {
        const val FILM_ID = "film_id"
        const val IMAGE_ID_RES = "imageIdRes"
        const val NAME_ID_RES = "nameIdRes"
        const val DESCRIPTION_ID_RES = "descriptionIdRes"
        const val IS_FAVOURITE = "isFavourite"
        const val COMMENT = "comment"
    }
}