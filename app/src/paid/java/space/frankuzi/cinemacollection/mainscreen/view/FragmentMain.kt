package space.frankuzi.cinemacollection.mainscreen.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.mainactivityview.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentMainBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.mainscreen.adapter.FilmItemsPaginationAdapter
import space.frankuzi.cinemacollection.mainscreen.adapter.RetryLoadListener
import space.frankuzi.cinemacollection.mainscreen.viewmodel.MainViewModel
import space.frankuzi.cinemacollection.structs.FilmClickListener
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholder.viewholderanim.CustomItemAnimator
import space.frankuzi.cinemacollection.viewholder.viewholderdecor.ViewHolderOffset
import javax.inject.Inject


class FragmentMain : Fragment(R.layout.fragment_main) {
    @Inject lateinit var _mainViewModelFactory: MainViewModel.MainViewModelFactory

    private val mainViewModel: MainViewModel by viewModels { _mainViewModelFactory }
    private val detailViewModel: DetailsViewModel by activityViewModels()
    private lateinit var _mainFragmentBinding: FragmentMainBinding
    private lateinit var _searchView: SearchView

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

        App.applicationComponentInstance?.let {

            it.mainActivityComponentFactory().create().mainFragmentComponentFactory().create().inject(this)
        }

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
        }

        mainViewModel.isRefreshing.observe(viewLifecycleOwner) {
            _mainFragmentBinding.refresh.isRefreshing = it
            _searchView.isIconified = true;
        }

        mainViewModel.isLastPage.observe(viewLifecycleOwner) {
            _adapter.isLastPages = it
        }

        mainViewModel.loadError.observe(viewLifecycleOwner) {
            val errorText = when (it.errorId) {
                R.string.error_code -> {
                    getString(it.errorId, it.parameters)
                }
                else -> {
                    getString(it.errorId)
                }
            }
            _adapter.setError(errorText)
            val mainActivity = activity as MainActivity
            mainActivity.showSnackBar(errorText, SnackBarAction(R.string.retry) {
                retryLoadFilms()
            })

            _mainFragmentBinding.refresh.isRefreshing = false
        }

        mainViewModel.refreshError.observe(viewLifecycleOwner) {
            val errorText = when (it.errorId) {
                R.string.error_code -> {
                    getString(it.errorId, it.parameters)
                }
                else -> {
                    getString(it.errorId)
                }
            }
            _adapter.isLastPages = true
            val mainActivity = activity as MainActivity

            mainActivity.showSnackBar(errorText, SnackBarAction(R.string.retry) {
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

                if (lastVisibleItemPosition == _adapter.itemCount - 1 && mainViewModel.isLastPage.value == false) {
                    Log.i("", "refresh")
                    mainViewModel.loadNextPage()
                }
            }
        })
    }

    private fun initToolbar() {

        val toolbar = _mainFragmentBinding.toolbar
        toolbar.setTitle(R.string.general)

        _searchView = toolbar.menu.findItem(R.id.search)?.actionView as SearchView

        _searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                   mainViewModel.searchFilmsByName(it)
                }
                return false
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    mainViewModel.searchFilmsByName(it)
                }
                return false
            }
        })

        _searchView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) = Unit

            override fun onViewDetachedFromWindow(v: View?) {
                mainViewModel.checkIsLastPages()
            }
        })
    }

    private fun retryLoadFilms() {
        mainViewModel.retryLoadFilm()
        _adapter.enableLoading()
    }

    fun setRecycleViewOnStart() {
        _mainFragmentBinding.itemsContainer.stopScroll()
        _mainFragmentBinding.itemsContainer.layoutManager?.scrollToPosition(0)
    }
}