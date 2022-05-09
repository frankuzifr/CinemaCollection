package space.frankuzi.cinemacollection

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.fragments.FragmentFavourites
import space.frankuzi.cinemacollection.fragments.FragmentMain

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        setMainFragment()
        initBottomNavigationBar()
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

//        bottomNavigation.setOnItemReselectedListener {
//            true
//        }
    }

    private fun setMainFragment() {

//        val isPop = supportFragmentManager.popBackStackImmediate("Main", 0)
//
//        if (!isPop) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.items_container, FragmentMain())
                .addToBackStack("Main")
                .commit()
//        }
        Log.i("", supportFragmentManager.backStackEntryCount.toString())
    }

    private fun setFavouritesFragment() {

//        val isPop = supportFragmentManager.popBackStackImmediate("Favourites", 0)
//
//        if (!isPop) {

            supportFragmentManager.beginTransaction()
                .replace(R.id.items_container, FragmentFavourites())
                .addToBackStack("Favourites")
                .commit()
        //}
        Log.i("", supportFragmentManager.backStackEntryCount.toString())
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {

            val bottomNavigation = _binding.bottomNavigation
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val name = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name

            when (name) {
                "Main" -> bottomNavigation.selectedItemId = R.id.favourites
                "Favourites" -> bottomNavigation.selectedItemId = R.id.main
                else -> {
                    bottomNavigation.selectedItemId = bottomNavigation.selectedItemId
                }
            }
            Log.i("", name.toString())

        } else {
            ExitDialog {
                super.onBackPressed()
            }.show(supportFragmentManager, "dialog")
        }
        Log.i("", supportFragmentManager.backStackEntryCount.toString())
    }
}