package space.frankuzi.cinemacollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import space.frankuzi.cinemacollection.custombackstack.CustomBackStack
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.fragments.FragmentFavourites
import space.frankuzi.cinemacollection.fragments.FragmentMain
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.viewmodel.DetailsViewModel

class MainActivity : AppCompatActivity() {
    private val _detailViewModel: DetailsViewModel by viewModels(factoryProducer = {
        object : AbstractSavedStateViewModelFactory(this, null){
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                return if (modelClass == DetailsViewModel::class.java) {
                    val application = application as App

                    val api = application.filmsApi
                    val database = application.database

                    DetailsViewModel(api, database.getFilmsDao()) as T
                } else {
                    throw ClassNotFoundException()
                }
            }

        }
    })

    private val _customBackStack = CustomBackStack()
    private lateinit var _binding: ActivityMainBinding
    private val _fragmentMain = FragmentMain()
    private var _fragmentFavourites = FragmentFavourites()
    private lateinit var _bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>
    private var _filmItem: FilmItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        setMainFragment()
        initBottomNavigationBar()
        initBottomSheet()

        initSubscribers()
    }

    private fun initBottomSheet() {
        val bottomSheet = _binding.bottomSheet
        _bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet.designBottomSheet)
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        setFavouriteState()

        bottomSheet.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    _filmItem?.let { filmItem ->
                        onShareButtonClick(filmItem.name)
                    }
                }
                R.id.favourite -> {

                    _filmItem?.let { filmItem ->
                        _detailViewModel.onClickFavourite(filmItem)

                        val filmName = filmItem.name
                        showToastWithText(
                            this,
                            if (it.isChecked)
                                getString(R.string.film_added_to_favourites, filmName)
                            else
                                getString(R.string.film_removed_from_favourites, filmName)
                        )
                    }

                    it.isChecked = !it.isChecked
                }
            }
            true
        }

        bottomSheet.toolbar.setNavigationOnClickListener {
            closeDetail()
        }
    }

    private fun closeDetail() {
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        _binding.bottomSheet.appBar.setExpanded(true)
    }

    private fun onShareButtonClick(filmName: String?) {

        val sendMessageIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_message, filmName))
        }

        val sharedIntent = Intent.createChooser(sendMessageIntent, null)

        startActivity(sharedIntent)
    }

    private fun setFavouriteState() {
        _filmItem?.let {

            val favouriteItem = _binding.bottomSheet.toolbar.menu.getItem(0)
            favouriteItem.isChecked = it.isFavourite

            if (it.isFavourite) {
                favouriteItem.setIcon(R.drawable.ic_baseline_favorite_24)
                favouriteItem.setTitle(R.string.no_liked)

            } else {
                favouriteItem.setIcon(R.drawable.ic_baseline_favorite_border_24)
                favouriteItem.setTitle(R.string.liked)
            }
        }
    }

    private fun initSubscribers() {

        _detailViewModel.selectedItem.observe(this) { filmItem ->
            _filmItem = filmItem
            _binding.bottomSheet.let {
                it.filmDescription.text = filmItem.description
                it.toolbar.title = filmItem.name
                it.collapsingToolbar.title = filmItem.name
                Glide.with(this)
                    .load(filmItem.imageUrl)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .centerCrop()
                    .into(it.filmImage)
                //it.filmImage.setImageResource(filmItem.imageIdRes)
            }
            setFavouriteState()
            _bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        _detailViewModel.favouriteToggleIsChanged.observe(this) {
            setFavouriteState()
        }

        _detailViewModel.loadError.observe(this) {
            showSnackBar(it, SnackBarAction(R.string.retry) {
                retryLoadDescription()
            })
        }
    }

    private fun retryLoadDescription() {
        _detailViewModel.retryLoadDescription()
    }

    fun showSnackBar(snackBarText: String, snackBarAction: SnackBarAction) {
        cancelToast()

        Snackbar.make(_binding.root, snackBarText, Snackbar.LENGTH_LONG)
            .setAction(getString(snackBarAction.actionNameId)) {
                snackBarAction.action.invoke()
            }
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setAnchorView(_binding.bottomNavigation)
            .show()
    }

    private fun initBottomNavigationBar() {

        val bottomNavigation = _binding.bottomNavigation

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.main -> {
                    setMainFragment()
                }
                R.id.favourites -> {
                    setFavouritesFragment()
                }
            }
            true
        }

        bottomNavigation.setOnItemReselectedListener {
            true
        }
    }

    private fun setMainFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentMain)
            .commit()

        _customBackStack.addToBackStack("Main", R.id.main)

    }

    private fun setFavouritesFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentFavourites)
            .commit()

        _customBackStack.addToBackStack("Favourites", R.id.favourites)
    }

    override fun onBackPressed() {

        if (_bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeDetail()
            return
        }

        val popFromBackStack = _customBackStack.popFromBackStack()

        if (popFromBackStack != null) {

            val bottomNavigation = _binding.bottomNavigation
            bottomNavigation.selectedItemId = popFromBackStack.navigationBarId

        } else {
            ExitDialog {
                super.onBackPressed()
            }.show(supportFragmentManager, "dialog")
        }
    }
}