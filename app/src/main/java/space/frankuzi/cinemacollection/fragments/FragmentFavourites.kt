package space.frankuzi.cinemacollection.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemsPaginationAdapter
import space.frankuzi.cinemacollection.adapter.RetryLoadListener
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentFavouritesBinding
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.viewmodel.FavouritesViewModel

class FragmentFavourites : Fragment(R.layout.fragment_favourites) {
    private val favouritesViewModel: FavouritesViewModel by viewModels()

    private lateinit var _itemContainer: RecyclerView
    private lateinit var _binding: FragmentFavouritesBinding
    private var _fragmentDetail: FragmentDetail? = null

    private val _adapter = FilmItemsPaginationAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
//                film.isSelected = true
//
//                _fragmentDetail = FragmentDetail()
//                val arguments = Bundle()
//                arguments.putInt(FragmentDetail.FILM_ID, position)
//                arguments.putBoolean(FragmentDetail.IS_FROM_FAVOURITES, true)
//                _fragmentDetail?.arguments = arguments
//
//                _fragmentDetail?.let {
//                    setFilmDetail(it)
//                }
//
//                _itemContainer.adapter?.notifyItemChanged(position)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
            val filmName = film.name

            favouritesViewModel.onItemRemoveFromFavourite(film)
            //showToastWithText(requireActivity(), resources.getString(R.string.film_removed_from_favourites, filmName))

            val mainActivity = activity as MainActivity
            mainActivity.showSnackBar(
                getString(R.string.film_removed_from_favourites, filmName),
                SnackBarAction(
                    actionNameId = R.string.cancel,
                    action = {
                        favouritesViewModel.onItemRemoveCancel(film, position)
                        _itemContainer.adapter?.notifyItemInserted(position)
                    }
                ))

            _itemContainer.adapter?.notifyItemRemoved(position)
        }
    }, object : RetryLoadListener{
        override fun onRetryLoadClickListener() {

        }

    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFavouritesBinding.inflate(layoutInflater)

        _fragmentDetail?.let { setFilmDetail(it) }

        initSubscribers()
        initRecycleView(view)
        initToolbar(view)
        initResultListener()

        favouritesViewModel.loadFavouritesFilms()
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

        _itemContainer = _binding.itemsContainer

        _itemContainer.layoutManager = layoutManager

        _itemContainer.adapter = _adapter

        _itemContainer.addItemDecoration(ViewHolderOffset(20))
    }

    private fun initSubscribers() {
        favouritesViewModel.favouritesFilms.observe(viewLifecycleOwner) {
            _adapter.setItems(it)
        }

        favouritesViewModel.itemRemoved.observe(viewLifecycleOwner) {
            //_adapter.notifyItemRemoved()
        }

        favouritesViewModel.itemRemoveCanceled.observe(viewLifecycleOwner) {

        }
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