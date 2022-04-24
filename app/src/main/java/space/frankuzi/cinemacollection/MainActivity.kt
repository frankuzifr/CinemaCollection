package space.frankuzi.cinemacollection

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

    private var _backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        _recyclerView = _binding.container

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3

        val layoutManager = GridLayoutManager(this, spanCount)
        _recyclerView.layoutManager = layoutManager
        _recyclerView.adapter = FilmItemAdapter(FilmsData.films, this, object : FilmClickListener {
            override fun onFilmClickListener(film: FilmItem, position: Int) {
                film.isSelected = true

                val intent = Intent(baseContext, FilmDetailActivity::class.java)
                intent.putExtra(FilmDetailActivity.FILM_ID, position)
                intent.putExtra(FilmDetailActivity.IMAGE_ID_RES, film.imageIdRes)
                intent.putExtra(FilmDetailActivity.NAME_ID_RES, film.nameIdRes)
                intent.putExtra(FilmDetailActivity.DESCRIPTION_ID_RES, film.descriptionIdRes)
                intent.putExtra(FilmDetailActivity.IS_FAVOURITE, film.isFavourite)
                startActivityForResult(intent, DETAIL_REQUEST_CODE)

                _recyclerView.adapter?.notifyItemChanged(position)
            }

            override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
                val filmName = resources.getString(film.nameIdRes)
                if (film.isFavourite) {
                    film.isFavourite = false
                    FilmsData.favouriteFilms.remove(film)
                    showToastWithText(baseContext, resources.getString(R.string.film_removed_from_favourites, filmName))
                } else {
                    film.isFavourite = true
                    FilmsData.favouriteFilms.add(film)
                    showToastWithText(baseContext, resources.getString(R.string.film_added_to_favourites, filmName))
                }
            }
        })
        _recyclerView.addItemDecoration(ViewHolderOffset(15))

        val toolbar = _binding.toolbar

        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.favourites -> {
                    val intent = Intent(this, FavouriteFilmsActivity::class.java)
                    startActivityForResult(intent, FAVOURITES_REQUEST_CODE)
                }
            }
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != DETAIL_REQUEST_CODE && requestCode != FAVOURITES_REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        if (resultCode != RESULT_OK)
            return

        if (data == null)
            return

        if (requestCode == DETAIL_REQUEST_CODE) {
            val filmId = data.getIntExtra(FilmDetailActivity.FILM_ID, 0)
            val isFavourite = data.getBooleanExtra(FilmDetailActivity.IS_FAVOURITE, false)
            val comment = data.getStringExtra(FilmDetailActivity.COMMENT)

            val filmItem = FilmsData.films[filmId]

            if (filmItem.isFavourite != isFavourite)
                if (isFavourite) {
                    FilmsData.favouriteFilms.add(filmItem)
                    filmItem.isFavourite = true
                } else {
                    FilmsData.favouriteFilms.remove(filmItem)
                    filmItem.isFavourite = false
                }

            _recyclerView.adapter?.notifyItemChanged(filmId)

            Log.i("Film name", resources.getString(filmItem.nameIdRes))
            Log.i("Favourite", isFavourite.toString())
            Log.i("Comment", comment.toString())
        }

        if (requestCode == FAVOURITES_REQUEST_CODE) {
            _recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onBackPressed() {
        ExitDialog {
            super.onBackPressed()
        }.show(supportFragmentManager, "dialog")
    }

    companion object {
        const val DETAIL_REQUEST_CODE: Int = 99
        const val FAVOURITES_REQUEST_CODE: Int = 100
    }
}