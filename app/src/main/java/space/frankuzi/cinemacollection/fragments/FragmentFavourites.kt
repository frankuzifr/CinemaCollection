package space.frankuzi.cinemacollection.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemAdapter
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset

class FragmentFavourites : Fragment(R.layout.fragment_favourites) {
    private lateinit var _itemContainer: RecyclerView
    private var _fragmentDetail: FragmentDetail? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _fragmentDetail?.let { setFilmDetail(it) }

        initRecycleView(view)
        initToolbar(view)
        initResultListener()
    }

    fun closeDetail() {
        _fragmentDetail?.closeDetail()
    }

    private fun initResultListener() {

        childFragmentManager.setFragmentResultListener(FragmentDetail.REQUEST_KEY_DETAIL, this) {_, result ->
            val filmId = result.getInt(FragmentDetail.FILM_ID)
            _itemContainer.adapter?.notifyItemChanged(filmId)
            _fragmentDetail = null
        }
    }

    private fun initRecycleView(view: View) {

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)

        _itemContainer = view.findViewById(R.id.items_container)

        _itemContainer.layoutManager = layoutManager

        _itemContainer.adapter = FilmItemAdapter(object : FilmClickListener {
            override fun onFilmClickListener(film: FilmItem, position: Int) {
                film.isSelected = true

                _fragmentDetail = FragmentDetail()
                val arguments = Bundle()
                arguments.putInt(FragmentDetail.FILM_ID, position)
                arguments.putBoolean(FragmentDetail.IS_FROM_FAVOURITES, true)
                _fragmentDetail?.arguments = arguments

                _fragmentDetail?.let {
                    setFilmDetail(it)
                }

                _itemContainer.adapter?.notifyItemChanged(position)
            }

            override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
                val filmName = resources.getString(film.nameIdRes)

                film.isFavourite = false
                FilmsData.favouriteFilms.remove(film)
                //showToastWithText(requireActivity(), resources.getString(R.string.film_removed_from_favourites, filmName))

                val mainActivity = activity as MainActivity
                mainActivity.showSnackBar(
                    getString(R.string.film_removed_from_favourites, filmName),
                    SnackBarAction(
                        actionNameId = R.string.cancel,
                        action = {
                            film.isFavourite = true
                            FilmsData.favouriteFilms.add(position, film)
                            _itemContainer.adapter?.notifyItemInserted(position)
                            Log.i("", filmName)
                        }
                    ))

                _itemContainer.adapter?.notifyItemRemoved(position)
            }
        })

        _itemContainer.addItemDecoration(ViewHolderOffset(20))
    }

    private fun setFilmDetail(fragmentDetail: FragmentDetail) {

        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_from_down_to_up, R.anim.fragment_from_up_to_down, R.anim.fragment_from_down_to_up, R.anim.fragment_from_up_to_down)
            .replace(R.id.film_detail_fragment_container, fragmentDetail)
            .addToBackStack("Detail")
            .commit()
    }

    private fun initToolbar(view: View) {

        val toolbar = view.findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.favourites)
    }
}