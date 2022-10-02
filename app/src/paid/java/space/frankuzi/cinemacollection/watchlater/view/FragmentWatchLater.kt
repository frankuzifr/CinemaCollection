package space.frankuzi.cinemacollection.watchlater.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentWatchLaterBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.structs.FilmClickListener
import space.frankuzi.cinemacollection.viewholder.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholder.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.watchlater.adapter.WatchLaterFilmsAdapter
import space.frankuzi.cinemacollection.watchlater.viewmodel.WatchLaterViewModel
import javax.inject.Inject

class FragmentWatchLater: Fragment(R.layout.fragment_watch_later) {

    @Inject lateinit var _watchLaterViewModelFactory: WatchLaterViewModel.WatchLaterViewModelFactory

    private val watchLaterViewModel: WatchLaterViewModel by viewModels { _watchLaterViewModelFactory }

    private val _adapter = WatchLaterFilmsAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
            detailViewModel.setItem(film)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
            watchLaterViewModel.onClickFavourite(film)
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

        App.applicationComponentInstance?.let {

            it.mainActivityComponentFactory().create().watchLaterComponentFactory()
                .create().inject(this)
        }

        initRecycleView()
        initToolbar()
        initSubscribers()

        watchLaterViewModel.loadWatchLaterFilms()
    }

    private fun initSubscribers() {
        watchLaterViewModel.watchLaterFilms.observe(viewLifecycleOwner) {
            _adapter.setItems(it)
        }

        detailViewModel.watchLaterChanged.observe(viewLifecycleOwner) {
            _adapter.changeItem(it)
        }

        detailViewModel.favouriteStateChanged.observe(viewLifecycleOwner) {
            _adapter.changeItem(it)
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