package space.frankuzi.cinemacollection.mainScreen.view

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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.structs.FilmClickListener
import space.frankuzi.cinemacollection.mainScreen.adapter.FilmItemsPaginationAdapter
import space.frankuzi.cinemacollection.mainScreen.adapter.RetryLoadListener
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentMainBinding
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholder.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholder.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.mainScreen.viewmodel.MainViewModel
import space.frankuzi.cinemacollection.structs.ErrorType

class FragmentMain : Fragment(R.layout.fragment_main) {
    private val mainViewModel: MainViewModel by viewModels(factoryProducer = {
        object : AbstractSavedStateViewModelFactory(this, null){
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return if (modelClass == MainViewModel::class.java) {
                    val application = activity?.application as App

                    val api = application.filmsApi
                    val database = application.database

                    MainViewModel(api, database) as T
                } else {
                    throw ClassNotFoundException()
                }
            }

        }
    })
    private val detailViewModel: DetailsViewModel by activityViewModels()

    private lateinit var _mainFragmentBinding: FragmentMainBinding

    private val _adapter = FilmItemsPaginationAdapter(object : FilmClickListener {
        override fun onFilmClickListener(film: FilmItem, position: Int) {
            detailViewModel.setItem(film)
        }

        override fun onFilmFavouriteClickListener(film: FilmItem, position: Int) {
            mainViewModel.onClickFavourite(film)
        }
    }, object : RetryLoadListener {
        override fun onRetryLoadClickListener() {
            retryLoadFilms()
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mainFragmentBinding = FragmentMainBinding.inflate(layoutInflater)
        return _mainFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()
        initToolbar()
        initSubscribers()
        initSwipeToRefresh()

        mainViewModel.loadFilms()
    }

    private fun initSwipeToRefresh() {
        _mainFragmentBinding.refresh.setOnRefreshListener {
            mainViewModel.refreshFilms()
        }

        _mainFragmentBinding.refresh.setColorSchemeResources(
            R.color.refreshArrow
        )

        _mainFragmentBinding.refresh.setProgressBackgroundColorSchemeResource(
            R.color.refreshBackground
        )
    }

    private fun initSubscribers() {
        mainViewModel.films.observe(viewLifecycleOwner) {
            _adapter.setItems(it)

            _mainFragmentBinding.refresh.isRefreshing = false
        }

        mainViewModel.isLastFilmsPages.observe(viewLifecycleOwner) {
            _adapter.isLastPages = it
        }

        mainViewModel.loadError.observe(viewLifecycleOwner) {
            val errorMessage =
                if (it.errorType == ErrorType.ConnectionError)
                    getString(R.string.network_error)
                else
                    getString(R.string.error_code, it.errorCode.toString())

            _adapter.setError(errorMessage)
            val mainActivity = activity as MainActivity
            mainActivity.showSnackBar(errorMessage, SnackBarAction(R.string.retry) {
                retryLoadFilms()
            })

            _mainFragmentBinding.refresh.isRefreshing = false
        }

        mainViewModel.refreshError.observe(viewLifecycleOwner) {
            _adapter.isLastPages = true
            val mainActivity = activity as MainActivity
            val errorMessage =
                if (it.errorType == ErrorType.ConnectionError)
                    getString(R.string.network_error)
                else
                    getString(R.string.error_code, it.errorCode.toString())


            mainActivity.showSnackBar(errorMessage, SnackBarAction(R.string.retry) {
                _mainFragmentBinding.refresh.isRefreshing = true
                retryLoadFilms()
            })

            _mainFragmentBinding.refresh.isRefreshing = false
        }

        mainViewModel.filmItemChanged.observe(viewLifecycleOwner) {
            _adapter.notifyItemChanged(it)
        }

        detailViewModel.favouriteStateChanged.observe(viewLifecycleOwner) {
            mainViewModel.updateFilm(it)
        }
    }

    private fun initRecycleView() {

        val orientation = this.resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3
        val layoutManager = GridLayoutManager(requireActivity(), spanCount)

//        if (mainViewModel.listState != null)
//            layoutManager.onRestoreInstanceState(mainViewModel.listState)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(p0: Int): Int
            {
                return when (_adapter.getItemViewType(p0))
                {
                    FilmItemsPaginationAdapter.TYPE_FOOTER -> layoutManager.spanCount
                    FilmItemsPaginationAdapter.TYPE_ITEMS -> 1
                    else -> -1
                }
            }
        }

        _mainFragmentBinding.itemsContainer.layoutManager = layoutManager
        _mainFragmentBinding.itemsContainer.adapter = _adapter
        _mainFragmentBinding.itemsContainer.addItemDecoration(ViewHolderOffset(20))
        _mainFragmentBinding.itemsContainer.itemAnimator = CustomItemAnimator()

        _mainFragmentBinding.itemsContainer.addOnScrollListener(object : OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == _adapter.itemCount - 1 && mainViewModel.isLastFilmsPages.value == false) {

                    mainViewModel.loadNextPage()
                }
            }
        })
    }

    private fun initToolbar() {

        val toolbar = _mainFragmentBinding.include.toolbar
        toolbar.setTitle(R.string.general)
    }

    private fun retryLoadFilms() {
        mainViewModel.loadNextPage()
        _adapter.setLoading()
    }

    fun setRecycleViewOnStart() {
        _mainFragmentBinding.itemsContainer.stopScroll()
        _mainFragmentBinding.itemsContainer.layoutManager?.scrollToPosition(0)
    }
}