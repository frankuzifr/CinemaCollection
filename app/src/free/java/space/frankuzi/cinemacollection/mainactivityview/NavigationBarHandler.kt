package space.frankuzi.cinemacollection.mainactivityview

import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.mainscreen.view.FragmentMain
import space.frankuzi.cinemacollection.watchlater.view.FragmentWatchLater

class NavigationBarHandler(
    private val mainActivityController: MainActivityController
) {

    init {
        initBottomNavigationBar()
    }

    private val _fragmentMain = FragmentMain()
    private val _fragmentWatchLater = FragmentWatchLater()

    private fun initBottomNavigationBar() {

        val bottomNavigation = mainActivityController.mainBinding.bottomNavigation

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.main -> {
                    mainActivityController.mainActivity.dismissSnackBar()
                    setMainFragment()
                }
                R.id.watch_later -> {
                    mainActivityController.mainActivity.dismissSnackBar()
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
                R.id.watch_later -> {
                    _fragmentWatchLater.setRecycleViewOnStart()
                }
            }
            true
        }
    }

    fun setMainFragment() {

        mainActivityController.mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentMain)
            .commit()

        mainActivityController.customBackStack.addToBackStack("Main", R.id.main)

    }

    private fun setWatchLaterFragment() {

        mainActivityController.mainActivity.supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentWatchLater)
            .commit()

        mainActivityController.customBackStack.addToBackStack("WatchLater", R.id.watch_later)
    }
}