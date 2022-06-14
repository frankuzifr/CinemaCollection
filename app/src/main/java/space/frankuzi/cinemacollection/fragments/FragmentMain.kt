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
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.adapter.FilmItemsPaginationAdapter
import space.frankuzi.cinemacollection.adapter.RetryLoadListener
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentMainBinding
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewholderdecor.ViewHolderOffset
import space.frankuzi.cinemacollection.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.viewmodel.MainViewModel

class FragmentMain : Fragment(R.layout.fragment_main) {
    private val mainViewModel: MainViewModel by viewModels()
    private val detailViewModel: DetailsViewModel by activityViewModels()

    private lateinit var _recyclerView: RecyclerView
    private var _fragmentDetail: FragmentDetail? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //_fragmentDetail?.let { setFilmDetail(it) }
        _mainFragmentBinding = FragmentMainBinding.inflate(layoutInflater)
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
//            val index = FilmsData.films.indexOf(it)
//            _adapter.notifyItemChanged(index)
        }

        mainViewModel.isLastFilmsPages.observe(viewLifecycleOwner) {
            _adapter.isLastPages = it
        }

        mainViewModel.loadError.observe(viewLifecycleOwner) {
            _adapter.setError(it)
            val mainActivity = activity as MainActivity
            mainActivity.showSnackBar(it, SnackBarAction(R.string.retry) {
                retryLoadFilms()
            })
        }

//        detailViewModel.selectedItem.observe(viewLifecycleOwner) {
//            it.isSelected = true
            //filmTitle.setTextColor(activity.resources.getColor(film.titleColorId))

//            _fragmentDetail = FragmentDetail()
//
//            _fragmentDetail?.let { fragment ->
//                //setFilmDetail(fragment)
//            }

            //_itemContainer.adapter?.notifyItemChanged(position)
            //detailViewModel.setItem(film)
//        }

        detailViewModel.favouriteToggleIsChanged.observe(viewLifecycleOwner) {
//            val index = FilmsData.films.indexOf(it)
//            _adapter.notifyItemChanged(index)
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

        _recyclerView = view.findViewById(R.id.items_container)

        _recyclerView.layoutManager = layoutManager

        _recyclerView.adapter = _adapter

        _recyclerView.addItemDecoration(ViewHolderOffset(20))

        _recyclerView.addOnScrollListener(object : OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition == _adapter.itemCount - 1 && mainViewModel.isLastFilmsPages.value == false) {

                    mainViewModel.loadFilms()
//                    if (!loading && !isLastPage) {
//
//                        loading = true;
//                        fetchData((++pageCount));
//                        // Увеличиваем на 1 pagecount при каждой прокрутке для получения данных со следующей страницы
//                        // make loading = false после загрузки данных
//                        // Вызовите mAdapter.notifyDataSetChanged (), чтобы обновить адаптер и макет
//                    }
                }
            }
        })
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

    private fun retryLoadFilms() {
        mainViewModel.retryLoadCurrentPage()
        _adapter.setLoading()
    }
}