package space.frankuzi.cinemacollection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
    private val _detailViewModel: DetailsViewModel by viewModels()

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
            _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun onShareButtonClick(filmName: String?) {

        Log.i("", "VAR")
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
            _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            _binding.bottomSheet.let {
                it.toolbar.title = filmItem.name
                it.collapsingToolbar.title = filmItem.name
                //it.filmImage.setImageResource(filmItem.imageIdRes)
                //it.filmDescription.setText(filmItem.descriptionIdRes)
            }
            setFavouriteState()
        }

        _detailViewModel.favouriteToggleIsChanged.observe(this) {
            setFavouriteState()
        }
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

        _fragmentMain.let {

            if (it.isVisible && it.childFragmentManager.backStackEntryCount > 0) {
                it.closeDetail()
                return
            }
        }

        _fragmentFavourites.let {

            if (it.isVisible && it.childFragmentManager.backStackEntryCount > 0) {
                it.closeDetail()
                return
            }
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