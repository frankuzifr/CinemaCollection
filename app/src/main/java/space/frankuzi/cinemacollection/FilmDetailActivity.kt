package space.frankuzi.cinemacollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import space.frankuzi.cinemacollection.data.Film
import space.frankuzi.cinemacollection.databinding.ActivityFilmDetailBinding

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
        binding.detailToolbar.setTitle(nameIdRes)
        binding.filmDescription.setText(descriptionIdRes)

        binding.detailToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}