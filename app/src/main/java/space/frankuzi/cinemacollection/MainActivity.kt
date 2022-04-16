package space.frankuzi.cinemacollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        for (i in 0..10) {
            val filmCard = layoutInflater.inflate(R.layout.film_view_card, null)
            val filmName = filmCard.findViewById<TextView>(R.id.film_title_label)
            filmName.text = "Атака джентельменов"
            val button =
                filmCard.findViewById<com.google.android.material.card.MaterialCardView>(R.id.film_card)

            button.setOnClickListener {
                Log.i("", "HI")
            }
            binding.container.addView(filmCard)
        }
    }
}