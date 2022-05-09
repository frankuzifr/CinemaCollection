package space.frankuzi.cinemacollection.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.showToastWithText

class FragmentDetail : Fragment(R.layout.fragment_detail_film) {
    private var _filmId: Int = 0
    private var _imageIdRes: Int = 0
    private var _nameIdRes: Int = 0
    private var _descriptionIdRes: Int = 0
    private var _isFavourite: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFilmIds()

        initImage(view)
        initToolbar(view)
        initDescription(view)
    }

    private fun initFilmIds() {

        _filmId = arguments?.getInt(FILM_ID) ?: return

        _imageIdRes = FilmsData.films[_filmId].imageIdRes
        _nameIdRes = FilmsData.films[_filmId].nameIdRes
        _descriptionIdRes = FilmsData.films[_filmId].descriptionIdRes
        _isFavourite = FilmsData.films[_filmId].isFavourite
    }

    private fun initImage(view: View) {

        val imageView = view.findViewById<ImageView>(R.id.film_image)

        imageView.setBackgroundResource(_imageIdRes)
    }

    private fun initToolbar(view: View) {

        val toolBar = view.findViewById<Toolbar>(R.id.toolbar)
        toolBar.setTitle(_nameIdRes)

        toolBar.setNavigationOnClickListener {
            val result = Bundle()
            result.putInt(FILM_ID, _filmId)
            parentFragmentManager.setFragmentResult(REQUEST_KEY_DETAIL, result)
            parentFragmentManager.popBackStack()
        }

        val favouriteItem = toolBar.menu.getItem(0)
        favouriteItem.isChecked = _isFavourite

        favouriteItem.setIcon(
            if (_isFavourite)
                R.drawable.ic_baseline_favorite_24
            else
                R.drawable.ic_baseline_favorite_border_24
        )

        favouriteItem.setTitle(
            if (_isFavourite)
                R.string.no_liked
            else
                R.string.liked
        )

        toolBar.setOnMenuItemClickListener {
            val filmItem = FilmsData.films[_filmId]
            when (it.itemId) {
                R.id.share -> onShareButtonClick(resources.getString(_nameIdRes))
                R.id.favourite -> {
                    val filmName = resources.getString(_nameIdRes)
                    if (it.isChecked){
                        it.isChecked = false
                        filmItem.isFavourite = false
                        FilmsData.favouriteFilms.remove(filmItem)
                        it.setIcon(R.drawable.ic_baseline_favorite_border_24)
                        it.setTitle(R.string.liked)
                        showToastWithText(
                            requireActivity(),
                            resources.getString(R.string.film_removed_from_favourites, filmName)
                        )
                    } else {
                        it.isChecked = true
                        filmItem.isFavourite = true
                        FilmsData.favouriteFilms.add(filmItem)
                        it.setIcon(R.drawable.ic_baseline_favorite_24)
                        it.setTitle(R.string.no_liked)
                        showToastWithText(
                            requireActivity(),
                            resources.getString(R.string.film_added_to_favourites, filmName)
                        )
                    }
                }
            }
            true
        }
    }

    private fun initDescription(view: View) {

        val textView = view.findViewById<TextView>(R.id.film_description)

        textView.setText(_descriptionIdRes)
    }

    private fun onShareButtonClick(filmName: String) {

        val sendMessageIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_message, filmName))
        }

        val sharedIntent = Intent.createChooser(sendMessageIntent, null)

        startActivity(sharedIntent)
    }

    companion object {
        const val FILM_ID = "filmId"
        const val REQUEST_KEY_DETAIL = "detail"
    }
}