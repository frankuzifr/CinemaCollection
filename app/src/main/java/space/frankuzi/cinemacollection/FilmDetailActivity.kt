package space.frankuzi.cinemacollection

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.ACTION
import android.util.Log
import space.frankuzi.cinemacollection.data.Film
import space.frankuzi.cinemacollection.databinding.ActivityFilmDetailBinding
import java.lang.Exception

class FilmDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilmDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilmDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val imageIdRes = intent.getIntExtra("imageIdRes", 0)
        val nameIdRes = intent.getIntExtra("nameIdRes", 0)
        val descriptionIdRes = intent.getIntExtra("descriptionIdRes", 0)
        val isFavourite = intent.getBooleanExtra("isFavourite", false)

        binding.filmImage.setBackgroundResource(imageIdRes)
        val detailToolbar = binding.detailToolbar
        detailToolbar.setTitle(nameIdRes)
        binding.filmDescription.setText(descriptionIdRes)

        detailToolbar.setNavigationOnClickListener {
            finish()
        }

        detailToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> onShareButtonClick(resources.getString(nameIdRes))
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
}