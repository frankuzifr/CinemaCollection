package space.frankuzi.cinemacollection.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FavouritesFilmsAdapter
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentFavouritesBinding
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.viewmodel.FavouritesViewModel

class FragmentFavourites : Fragment(R.layout.fragment_favourites) {
    private val favouritesViewModel: FavouritesViewModel by viewModels(factoryProducer = {
        object : AbstractSavedStateViewModelFactory(this, null){
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return if (modelClass == FavouritesViewModel::class.java) {
                    val application = activity?.application as App
                    val database = application.database

                    FavouritesViewModel(database) as T
                } else {
                    throw ClassNotFoundException()
                }
            }
        }
    })

    private val detailViewModel: DetailsViewModel by activityViewModels()
    private lateinit var _itemContainer: RecyclerView
    private lateinit var _binding: FragmentFavouritesBinding

    private val _adapter = FavouritesFilmsAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
            detailViewModel.setItem(film)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
            val filmName = film.name

            favouritesViewModel.onItemRemoveFromFavourite(film)

            val mainActivity = activity as MainActivity
            mainActivity.showSnackBar(
                getString(R.string.film_removed_from_favourites, filmName),
                SnackBarAction(
                    actionNameId = R.string.cancel,
                    action = {
                        favouritesViewModel.onItemRemoveCancel(film, position)
                    }
                ))
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouritesBinding.inflate(layoutInflater)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSubscribers()
        initRecycleView()
        initToolbar()

        favouritesViewModel.loadFavouritesFilms()
    }


    private fun initRecycleView() {

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)

        _itemContainer = _binding.itemsContainer

        _itemContainer.layoutManager = layoutManager

        _itemContainer.adapter = _adapter

        _itemContainer.addItemDecoration(ViewHolderOffset(20))

        _itemContainer.itemAnimator = CustomItemAnimator()
    }

    private fun initSubscribers() {
        favouritesViewModel.favouritesFilms.observe(viewLifecycleOwner) {
            _adapter.setItems(it)
        }

        favouritesViewModel.itemRemoved.observe(viewLifecycleOwner) {
            _adapter.removeItem(it)
        }

        favouritesViewModel.itemRemoveCanceled.observe(viewLifecycleOwner) {
            _adapter.addItem(it.filmItem, it.position)
        }

        detailViewModel.filmChanged.observe(viewLifecycleOwner) {
            Log.i("", "KOKO")
            favouritesViewModel.onItemRemoveFromFavourite(it)
        }
    }

    private fun initToolbar() {

        val toolbar = _binding.include.toolbar
        toolbar.setTitle(R.string.favourites)
    }

    fun setRecycleViewOnStart() {
        _binding.itemsContainer.layoutManager?.scrollToPosition(0)
    }
}