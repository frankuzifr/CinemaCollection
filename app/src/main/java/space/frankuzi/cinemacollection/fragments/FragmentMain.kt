package space.frankuzi.cinemacollection.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemAdapter
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.FilmsData
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.viewmodel.MainViewModel

class FragmentMain : Fragment(R.layout.fragment_main) {
    private val mainViewModel: MainViewModel by viewModels()
    private val detailViewModel: DetailsViewModel by activityViewModels()

    private lateinit var _itemContainer: RecyclerView
    private var _fragmentDetail: FragmentDetail? = null

    private val _adapter = FilmItemAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
            detailViewModel.setItem(film)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
            mainViewModel.onClickFavourite(film)
        }
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //_fragmentDetail?.let { setFilmDetail(it) }

        initRecycleView(view)
        initToolbar(view)
        //initResultListener()
        initSubscribers()

        mainViewModel.loadFilms()
    }

    private fun initSubscribers() {
        mainViewModel.films.observe(viewLifecycleOwner) {
            _adapter.setItems(it)
        }

        mainViewModel.filmItemChanged.observe(viewLifecycleOwner) {
            val index = FilmsData.films.indexOf(it)
            _adapter.notifyItemChanged(index)
        }

        detailViewModel.selectedItem.observe(viewLifecycleOwner) {
            it.isSelected = true
            //filmTitle.setTextColor(activity.resources.getColor(film.titleColorId))

            _fragmentDetail = FragmentDetail()

            _fragmentDetail?.let { fragment ->
                //setFilmDetail(fragment)
            }

            //_itemContainer.adapter?.notifyItemChanged(position)
            //detailViewModel.setItem(film)
        }
    }

    fun closeDetail() {
        _fragmentDetail?.closeDetail()
    }
//
//    private fun initResultListener() {
//
//        childFragmentManager.setFragmentResultListener(FragmentDetail.REQUEST_KEY_DETAIL, this) {_, result ->
//            val filmId = result.getInt(FragmentDetail.FILM_ID)
//            _itemContainer.adapter?.notifyItemChanged(filmId)
//        }
//    }

    private fun initRecycleView(view: View) {

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)

        _itemContainer = view.findViewById(R.id.items_container)

        _itemContainer.layoutManager = layoutManager

        _itemContainer.adapter = _adapter

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
        toolbar.setTitle(R.string.general)
    }
}