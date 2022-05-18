package space.frankuzi.cinemacollection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import space.frankuzi.cinemacollection.custombackstack.CustomBackStack
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.fragments.FragmentFavourites
import space.frankuzi.cinemacollection.fragments.FragmentMain
import space.frankuzi.cinemacollection.structs.SnackBarAction

class MainActivity : AppCompatActivity() {
    private val _customBackStack = CustomBackStack()
    private lateinit var _binding: ActivityMainBinding
    private var _fragmentMain = FragmentMain()
    private var _fragmentFavourites = FragmentFavourites()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        setMainFragment()
        initBottomNavigationBar()
    }

    fun showSnackBar(snackBarText: String, snackBarAction: SnackBarAction) {
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