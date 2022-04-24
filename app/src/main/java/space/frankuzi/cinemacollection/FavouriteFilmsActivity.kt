package space.frankuzi.cinemacollection

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemAdapter
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.databinding.ActivityFavouriteFilmsBinding
import space.frankuzi.cinemacollection.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset

class FavouriteFilmsActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityFavouriteFilmsBinding
    private lateinit var _recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFavouriteFilmsBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)
        _recyclerView = _binding.container

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3

        val layoutManager = GridLayoutManager(this, spanCount)
        _recyclerView.layoutManager = layoutManager
        _recyclerView.adapter = FilmItemAdapter(FilmsData.favouriteFilms, this, object : FilmClickListener {
            override fun onFilmClickListener(film: FilmItem, position: Int) {
                film.isSelected = true

                val intent = Intent(baseContext, FilmDetailActivity::class.java)
                intent.putExtra(FilmDetailActivity.FILM_ID, position)
                intent.putExtra(FilmDetailActivity.IMAGE_ID_RES, film.imageIdRes)
                intent.putExtra(FilmDetailActivity.NAME_ID_RES, film.nameIdRes)
                intent.putExtra(FilmDetailActivity.DESCRIPTION_ID_RES, film.descriptionIdRes)
                intent.putExtra(FilmDetailActivity.IS_FAVOURITE, film.isFavourite)
                startActivityForResult(intent, MainActivity.DETAIL_REQUEST_CODE)

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

                _recyclerView.adapter?.notifyItemRemoved(position)
            }
        })
        _recyclerView.addItemDecoration(ViewHolderOffset(15))
        _recyclerView.itemAnimator = CustomItemAnimator()

        val toolbar = _binding.toolbar

        toolbar.setNavigationOnClickListener {
            sendResult()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != MainActivity.DETAIL_REQUEST_CODE) {
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

    private fun sendResult() {
        val intent = Intent()
        setResult(RESULT_OK, intent)
    }
}