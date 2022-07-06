package space.frankuzi.cinemacollection.watchlater.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentWatchLaterBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.favouritesScreen.adapter.FavouritesFilmsAdapter
import space.frankuzi.cinemacollection.favouritesScreen.viewmodel.FavouritesViewModel
import space.frankuzi.cinemacollection.structs.FilmClickListener
import space.frankuzi.cinemacollection.viewholder.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholder.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.watchlater.adapter.WatchLaterFilmsAdapter
import space.frankuzi.cinemacollection.watchlater.viewmodel.WatchLaterViewModel

class FragmentWatchLater: Fragment(R.layout.fragment_watch_later) {

    private val watchLaterViewModel: WatchLaterViewModel by viewModels(factoryProducer = {
        object : AbstractSavedStateViewModelFactory(this, null){
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return if (modelClass == WatchLaterViewModel::class.java) {
                    val application = activity?.application as App
                    val database = application.database

                    WatchLaterViewModel(database) as T
                } else {
                    throw ClassNotFoundException()
                }
            }
        }
    })

    private val _adapter = WatchLaterFilmsAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
            detailViewModel.setItem(film)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {

        }
    })

    private val detailViewModel: DetailsViewModel by activityViewModels()
    private lateinit var _binding: FragmentWatchLaterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchLaterBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()
        initToolbar()
        initSubscribers()

        watchLaterViewModel.loadWatchLaterFilms()
    }

    private fun initSubscribers() {
        watchLaterViewModel.watchLaterFilms.observe(viewLifecycleOwner) {
            _adapter.setItems(it)
        }
    }

    private fun initRecycleView() {

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)

        _binding.itemsContainer.layoutManager = layoutManager
        _binding.itemsContainer.adapter = _adapter
        _binding.itemsContainer.addItemDecoration(ViewHolderOffset(20))
        _binding.itemsContainer.itemAnimator = CustomItemAnimator()
    }

    private fun initToolbar() {

        val toolbar = _binding.include.toolbar
        toolbar.setTitle(R.string.watch_later)
    }

    fun setRecycleViewOnStart() {
        _binding.itemsContainer.stopScroll()
        _binding.itemsContainer.layoutManager?.scrollToPosition(0)
    }
}