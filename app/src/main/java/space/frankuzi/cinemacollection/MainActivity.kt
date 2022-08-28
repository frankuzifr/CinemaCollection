package space.frankuzi.cinemacollection

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.favouritesScreen.view.FragmentFavourites
import space.frankuzi.cinemacollection.mainScreen.view.FragmentMain
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.utils.FirebaseConfig
import space.frankuzi.cinemacollection.utils.broadcastreceiver.WatchLaterTimeComeInBroadcast
import space.frankuzi.cinemacollection.utils.cancelToast
import space.frankuzi.cinemacollection.utils.custombackstack.CustomBackStack
import space.frankuzi.cinemacollection.utils.showToastWithText
import space.frankuzi.cinemacollection.watchlater.datetime.*
import space.frankuzi.cinemacollection.watchlater.view.FragmentWatchLater
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var detailViewModelFactory: DetailsViewModel.DetailViewModelFactory
    @Inject lateinit var customBackStack: CustomBackStack
    @Inject lateinit var firebaseAnalytics: FirebaseAnalytics
    @Inject lateinit var alarmManager: AlarmManager

    private val _detailViewModel: DetailsViewModel by viewModels { detailViewModelFactory }

    private lateinit var _binding: ActivityMainBinding
    private lateinit var _bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    private val _fragmentMain = FragmentMain()
    private val _fragmentFavourites = FragmentFavourites()
    private val _fragmentWatchLater = FragmentWatchLater()
    private var _snackbar: Snackbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App._applicationComponentInstance?.mainActivityComponentFactory()?.create()?.inject(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        if (savedInstanceState == null)
            setMainFragment()

        initBottomNavigationBar()
        initBottomSheet()
        initSubscribers()
        createNotificationChannelWatchLater()
        createNotificationChannelForFirebase()
        tryOpenFromNotification()
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

    private fun sendAnalytics(filmItem: FilmItem) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, filmItem.id.toString())
            param(FirebaseAnalytics.Param.ITEM_NAME, filmItem.name.toString())
        }
    }

    private fun createNotificationChannelWatchLater() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.scheduled_viewing)
            val descriptionText = getString(R.string.schedule_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(WatchLaterTimeComeInBroadcast.WATCH_LATER_CHANNEL_ID, name, importance)
                .apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannelForFirebase() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.offers)
            val descriptionText = getString(R.string.schedule_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.offers), name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initBottomSheet() {
        val bottomSheet = _binding.bottomSheet
        _bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet.designBottomSheet)
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheet.toolbar.setNavigationOnClickListener {
            closeDetail()
        }

        bottomSheet.loadedStatus.retryButton.setOnClickListener {
            _binding.bottomSheet.loadedStatus.progressBar.visibility = View.VISIBLE
            _binding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
            _binding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE

            _detailViewModel.loadDescription()
        }

        _bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        _detailViewModel.closeDetail()
                        _binding.bottomSheet.appBar.setExpanded(true)
                        _binding.bottomSheet.filmDescription.text = ""
                    }
                    else -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })
    }

    private fun closeDetail() {
        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
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

        _detailViewModel.watchLaterChanged.observe(this) { filmItem ->
            setWatchLaterDate(filmItem)

            val intent = Intent(filmItem.id.toString(), null, this, WatchLaterTimeComeInBroadcast::class.java)
            intent.putExtra(WatchLaterTimeComeInBroadcast.FILM_ID, filmItem.id)
            intent.putExtra(WatchLaterTimeComeInBroadcast.FILM_NAME, filmItem.name)

            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            alarmManager.cancel(pendingIntent)

            filmItem.date?.let {
                alarmManager.set(AlarmManager.RTC_WAKEUP, it.time, pendingIntent)
            }
        }

        _detailViewModel.selectedItem.observe(this) { filmItem ->
            filmItem?.let { film ->
                sendAnalytics(film)
                openFilmDetail(film)
            }
        }

        _detailViewModel.descriptionLoaded.observe(this) { description->
            setDescription(description)
        }

        _detailViewModel.loadError.observe(this) {
            _binding.bottomSheet.loadedStatus.progressBar.visibility = View.INVISIBLE
            _binding.bottomSheet.loadedStatus.errorText.visibility = View.VISIBLE
            _binding.bottomSheet.loadedStatus.errorText.text = it
            _binding.bottomSheet.loadedStatus.retryButton.visibility = View.VISIBLE
        }
    }

    private fun setDescription(description: String?) {
        if (description == null)
            return

        _binding.bottomSheet.filmDescription.visibility = View.VISIBLE
        _binding.bottomSheet.filmDescription.text = description
        _binding.bottomSheet.loadedStatus.progressBar.visibility = View.INVISIBLE
        _binding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
        _binding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE
    }

    private fun openFilmDetail(film: FilmItem) {
        _binding.bottomSheet.let {
            it.filmDescription.visibility = View.INVISIBLE
            _binding.bottomSheet.loadedStatus.progressBar.visibility = View.VISIBLE
            _binding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
            _binding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE

            setWatchLaterDate(film)

            it.toolbar.title = film.name
            it.collapsingToolbar.title = film.name
            Glide.with(this)
                .load(film.imageUrl)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                .centerCrop()
                .into(it.filmImage)

            setFavouriteState()

            it.toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share -> {
                        onShareButtonClick(film.name)
                    }
                    R.id.favourite -> {

                        _detailViewModel.onClickFavourite(film)

                        val filmName = film.name
                        showToastWithText(
                            this,
                            if (!menuItem.isChecked)
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
        }

        _bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    private fun setWatchLaterDate(filmItem: FilmItem) {
        val bottomSheet = _binding.bottomSheet
        if (filmItem.date != null) {
            bottomSheet.watchLaterDateLabel.visibility = View.VISIBLE
            bottomSheet.watchLaterDateLabel.text = getString(R.string.viewing_sheduled_for, filmItem.getDateString())
        } else {
            bottomSheet.watchLaterDateLabel.visibility = View.INVISIBLE
        }
    }

    private fun openDateTimePicker() {

        val filmItem = _detailViewModel.selectedItem.value

        filmItem?.let {
            WatchLaterDialog(
                title = if (it.date == null) getString(R.string.no_viewing_time) else getString(R.string.viewing_sheduled_for, filmItem.getDateString()),
                watchLaterListener = object : WatchLaterListener {
                    override fun onChangeTimeClick() {
                        DatePickerFragment(object : DateSelectHandler {
                            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
                                TimePickerFragment(object : TimeSelectHandler{
                                    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                                        val dateTime = DateTime(
                                            dayOfMonth = dayOfMonth,
                                            month = month,
                                            year = year,
                                            hour = hourOfDay,
                                            minute = minute
                                        )

                                        if (it.date == null) {
                                            _detailViewModel.setDateTime(dateTime)
                                            it.date = dateTime.getDate()
                                            showToastWithText(baseContext, getString(R.string.film_added_to_sheduker_viewing, filmItem.name, filmItem.getDateString()))
                                        } else {
                                            _detailViewModel.changeDateTime(dateTime)
                                            it.date = dateTime.getDate()
                                            showToastWithText(baseContext, getString(R.string.changes_time_sheduler_viewing, filmItem.name, filmItem.getDateString()))
                                        }
                                    }

                                }).show(supportFragmentManager, "timePicker")
                            }

                        }).show(supportFragmentManager, "datePicker")
                    }

                    override fun onDeleteTimeClick() {
                        _detailViewModel.removeDateTime()
                        showToastWithText(baseContext, getString(R.string.film_remove_from_watch_later, filmItem.name))
                    }
                },
                withDelete = it.date != null
            ).show(supportFragmentManager, "watchLaterDialog")
        }
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

    private fun initBottomNavigationBar() {

        val bottomNavigation = _binding.bottomNavigation

        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId){
                R.id.main -> {
                    _snackbar?.dismiss()
                    setMainFragment()
                }
                R.id.favourites -> {
                    _snackbar?.dismiss()
                    setFavouritesFragment()
                }
                R.id.watch_later -> {
                    _snackbar?.dismiss()
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

        customBackStack.addToBackStack("Main", R.id.main)

    }

    private fun setFavouritesFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentFavourites)
            .commit()

        customBackStack.addToBackStack("Favourites", R.id.favourites)
    }

    private fun setWatchLaterFragment() {

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.items_container, _fragmentWatchLater)
            .commit()

        customBackStack.addToBackStack("WatchLater", R.id.watch_later)
    }

    override fun onBackPressed() {

        if (_bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            closeDetail()
            return
        }

        val popFromBackStack = customBackStack.popFromBackStack()

        if (popFromBackStack != null) {

            val bottomNavigation = _binding.bottomNavigation
            bottomNavigation.selectedItemId = popFromBackStack.navigationBarId

        } else {
            ExitDialog {
                super.onBackPressed()
            }.show(supportFragmentManager, "dialog")
        }
    }

    companion object {
        val FIREBASE_CONFIG = FirebaseConfig()
    }
}