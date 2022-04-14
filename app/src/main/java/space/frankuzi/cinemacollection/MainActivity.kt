package space.frankuzi.cinemacollection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        for (i in 0..5) {
            val filmCard = layoutInflater.inflate(R.layout.film_view_card, null)
            val filmName = filmCard.findViewById<TextView>(R.id.film_title_label)
            filmName.text = i.toString()
            binding.container.addView(filmCard)
        }
    }
}