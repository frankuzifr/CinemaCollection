package space.frankuzi.cinemacollection

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import space.frankuzi.cinemacollection.utils.custombackstack.CustomBackStack
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.favouritesScreen.view.FragmentFavourites
import space.frankuzi.cinemacollection.mainScreen.view.FragmentMain
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.utils.cancelToast
import space.frankuzi.cinemacollection.utils.showToastWithText
import space.frankuzi.cinemacollection.watchlater.datetime.*
import space.frankuzi.cinemacollection.watchlater.view.FragmentWatchLater

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

                    DetailsViewModel(api, database) as T
                } else {
                    throw ClassNotFoundException()
                }
            }
        }
    })

    private val _customBackStack = CustomBackStack()
    private lateinit var _binding: ActivityMainBinding
    private val _fragmentMain = FragmentMain()
    private val _fragmentFavourites = FragmentFavourites()
    private val _fragmentWatchLater = FragmentWatchLater()
    private lateinit var _bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

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

        bottomSheet.toolbar.setNavigationOnClickListener {
            closeDetail()
        }
    }

    private fun closeDetail() {
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        _detailViewModel.closeDetail()
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
        val favouriteItem = _binding.bottomSheet.toolbar.menu.getItem(0)

        _detailViewModel.selectedItem.value?.let {
            favouriteItem.isChecked = it.isFavourite
            favouriteItem.setIcon(it.favouriteIconId)
            favouriteItem.setTitle(it.favouriteLabelId)
        }
    }

    private fun initSubscribers() {

        _detailViewModel.selectedItem.observe(this) { filmItem ->
            filmItem?.let { film ->

                _binding.bottomSheet.let {
                    it.filmDescription.text = film.description
                    it.toolbar.title = film.name
                    it.collapsingToolbar.title = film.name
                    Glide.with(this)
                        .load(film.imageUrl)
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .error(R.drawable.ic_baseline_error_outline_24)
                        .centerCrop()
                        .into(it.filmImage)
                }

                setFavouriteState()

                _binding.bottomSheet.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.share -> {
                            onShareButtonClick(film.name)
                        }
                        R.id.favourite -> {

                            _detailViewModel.onClickFavourite(film)

                            val filmName = film.name
                            showToastWithText(
                                this,
                                if (!it.isChecked)
                                    getString(R.string.film_added_to_favourites, filmName)
                                else
                                    getString(R.string.film_removed_from_favourites, filmName)
                            )

                            setFavouriteState()
                        }
                        R.id.watch_later -> {
                            openDateTimePicker()
                        }
                    }
                    true
                }

                _bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        _detailViewModel.loadError.observe(this) {

            showToastWithText(this, it)
        }
    }

    private fun openDateTimePicker() {
        DatePickerFragment(object : DateSelectHandler {
            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
                TimePickerFragment(object : TimeSelectHandler{
                    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                        _detailViewModel.setDateTime(DateTime(
                            dayOfMonth = dayOfMonth,
                            month = month,
                            year = year,
                            hour = hourOfDay,
                            minute = minute
                        ))
                    }

                }).show(supportFragmentManager, "timePicker")
            }

        }).show(supportFragmentManager, "datePicker")
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
                R.id.watch_later -> {
                    setWatchLaterFragment()
                }
            }
            true
        }

        bottomNavigation.setOnItemReselectedListener {
            when (it.itemId){
                R.id.main -> {
                    _fragmentMain.setRecycleViewOnStart()
                }
                R.id.favourites -> {
                    _fragmentFavourites.setRecycleViewOnStart()
                }
                R.id.watch_later -> {
                    _fragmentWatchLater.setRecycleViewOnStart()
                }
            }
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

    private fun setWatchLaterFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentWatchLater)
            .commit()

        _customBackStack.addToBackStack("WatchLater", R.id.watch_later)
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