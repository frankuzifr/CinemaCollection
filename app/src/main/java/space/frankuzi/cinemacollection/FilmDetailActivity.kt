package space.frankuzi.cinemacollection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import space.frankuzi.cinemacollection.databinding.ActivityFilmDetailBinding
import java.lang.Exception

class FilmDetailActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityFilmDetailBinding
    private var _isFavourite: Boolean = false
    private var _filmId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFilmDetailBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        _filmId = intent.getIntExtra(FILM_ID, 0)
        val imageIdRes = intent.getIntExtra(IMAGE_ID_RES, 0)
        val nameIdRes = intent.getIntExtra(NAME_ID_RES, 0)
        val descriptionIdRes = intent.getIntExtra(DESCRIPTION_ID_RES, 0)

        _isFavourite = savedInstanceState?.getBoolean(IS_FAVOURITE) ?: intent.getBooleanExtra(IS_FAVOURITE, false)

        _binding.filmImage.setBackgroundResource(imageIdRes)
        val detailToolbar = _binding.detailToolbar
        detailToolbar.setTitle(nameIdRes)
        _binding.filmDescription.setText(descriptionIdRes)

        detailToolbar.setNavigationOnClickListener {
            sendResult()
            finish()
        }

        val favouriteItem = detailToolbar.menu.getItem(0)
        favouriteItem.isChecked = _isFavourite

        favouriteItem.setIcon(
            if (_isFavourite)
                R.drawable.ic_baseline_favorite_24
            else
                R.drawable.ic_baseline_favorite_border_24
        )

        detailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> onShareButtonClick(resources.getString(nameIdRes))
                R.id.favourite -> {
                    if (it.isChecked){
                        it.isChecked = false
                        _isFavourite = false
                        it.setIcon(R.drawable.ic_baseline_favorite_border_24)
                    } else {
                        it.isChecked = true
                        _isFavourite = true
                        it.setIcon(R.drawable.ic_baseline_favorite_24)
                    }
                }
                else -> throw Exception()
            }
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_FAVOURITE, _isFavourite)
        outState.putString(COMMENT, _binding.comment.editText?.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val comment = savedInstanceState.getString(COMMENT)
        _binding.comment.editText?.setText(comment)
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

    override fun onBackPressed() {
        sendResult()
        super.onBackPressed()
    }

    private fun sendResult() {
        val intent = Intent()
        intent.putExtra(FILM_ID, _filmId)
        intent.putExtra(IS_FAVOURITE, _isFavourite)
        intent.putExtra(COMMENT, _binding.comment.editText?.text.toString())
        setResult(RESULT_OK, intent)
    }

    companion object {
        const val FILM_ID = "filmId"
        const val IMAGE_ID_RES = "imageIdRes"
        const val NAME_ID_RES = "nameIdRes"
        const val DESCRIPTION_ID_RES = "descriptionIdRes"
        const val IS_FAVOURITE = "isFavourite"
        const val COMMENT = "comment"
    }
}