package space.frankuzi.cinemacollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemAdapter
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset


class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _recyclerView: RecyclerView
    val requestCode: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)
        _recyclerView = _binding.container

        val layoutManager = GridLayoutManager(this, 2)
        _recyclerView.layoutManager = layoutManager
        _recyclerView.adapter = FilmItemAdapter(FilmsData.films, this, object : FilmClickListener {
            override fun onFilmClickListener(film: FilmItem, position: Int) {
                _recyclerView.adapter?.notifyItemChanged(position)
            }

            override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {

            }
        })
        _recyclerView.addItemDecoration(ViewHolderOffset(15))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != this.requestCode) {
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

        _recyclerView.adapter?.notifyItemChanged(filmId)

        Log.i("Film name", resources.getString(FilmsData.films[filmId].nameIdRes))
        Log.i("Favourite", isFavourite.toString())
        Log.i("Comment", comment.toString())
    }
}