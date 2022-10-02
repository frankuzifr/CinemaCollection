package space.frankuzi.cinemacollection.mainactivityview

import android.app.AlarmManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.ExitDialog
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.utils.FirebaseConfig
import space.frankuzi.cinemacollection.utils.broadcastreceiver.WatchLaterTimeComeInBroadcast
import space.frankuzi.cinemacollection.utils.cancelToast
import space.frankuzi.cinemacollection.utils.custombackstack.CustomBackStack
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var detailViewModelFactory: DetailsViewModel.DetailViewModelFactory
    @Inject lateinit var customBackStack: CustomBackStack
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject lateinit var alarmManager: AlarmManager

    private val _detailViewModel: DetailsViewModel by viewModels { detailViewModelFactory }

    private lateinit var _binding: ActivityMainBinding
    private var _snackbar: Snackbar? = null

    private lateinit var mainActivityController: MainActivityController

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_CinemaCollection)
        super.onCreate(savedInstanceState)

        App.applicationComponentInstance?.mainActivityComponentFactory()?.create()?.inject(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        mainActivityController = MainActivityController(
            this,
            _binding,
            customBackStack,
            firebaseAnalytics,
            alarmManager,
            _detailViewModel
        )

        if (savedInstanceState == null)
            mainActivityController.navigationBarHandler.setMainFragment()

        tryOpenFromNotification()
    }

    fun showSnackBar(snackBarText: String, snackBarAction: SnackBarAction) {
        cancelToast()

        _snackbar = Snackbar.make(_binding.root, snackBarText, Snackbar.LENGTH_LONG)
            .setAction(getString(snackBarAction.actionNameId)) {
                snackBarAction.action.invoke()
            }
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setAnchorView(_binding.bottomNavigation)
        _snackbar?.show()
    }

    fun dismissSnackBar() {
        _snackbar?.dismiss()
    }

    private fun tryOpenFromNotification() {

        if (intent == null)
            return

        val value = intent.getStringExtra(WatchLaterTimeComeInBroadcast.FILM_ID)
        val filmId = value?.toIntOrNull()

        filmId?.let {
            _detailViewModel.setItemById(filmId)
        }

        intent = null
    }

    override fun onBackPressed() {

        if (mainActivityController.bottomSheetHandler.bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mainActivityController.bottomSheetHandler.closeDetail()
            return
        }

        val popFromBackStack = customBackStack.popFromBackStack()

        if (popFromBackStack != null) {

            val bottomNavigation = _binding.bottomNavigation
            bottomNavigation.selectedItemId = popFromBackStack.navigationBarId

        } else {
            ExitDialog().show(supportFragmentManager, "dialog")
        }
    }

    companion object {
        val FIREBASE_CONFIG = FirebaseConfig()
    }
}