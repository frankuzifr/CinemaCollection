package space.frankuzi.cinemacollection

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.utils.custombackstack.CustomBackStack
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.favouritesScreen.view.FragmentFavourites
import space.frankuzi.cinemacollection.mainScreen.view.FragmentMain
import space.frankuzi.cinemacollection.structs.SnackBarAction
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.utils.broadcastreceiver.WatchLaterTimeComeInBroadcast
import space.frankuzi.cinemacollection.utils.cancelToast
import space.frankuzi.cinemacollection.utils.showToastWithText
import space.frankuzi.cinemacollection.watchlater.datetime.*
import space.frankuzi.cinemacollection.watchlater.view.FragmentWatchLater
import java.util.*

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

        createNotificationChannel()

        if (intent == null)
            return

        val filmId = intent.getIntExtra("filmId", 0)

        if (filmId == 0)
            return

        _detailViewModel.setItemByWatchLater(filmId)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Проверка"
            val descriptionText = "Работает?"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
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

        _detailViewModel.watchLaterChanged.observe(this) { filmItem ->
            setWatchLaterDate(filmItem)

                filmItem.date?.let {
                    val intent = Intent(this, WatchLaterTimeComeInBroadcast::class.java)
                    intent.putExtra("filmId", filmItem.id)
                    intent.putExtra("filmName", filmItem.name)
                    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

                    val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                    alarmManager.set(AlarmManager.RTC_WAKEUP, it.time, pendingIntent)
            }

        }

        _detailViewModel.selectedItem.observe(this) { filmItem ->
            filmItem?.let { film ->
                openFilmDetail(film)
            }
        }

        _detailViewModel.loadError.observe(this) {

            showToastWithText(this, it)
        }
    }

    private fun openFilmDetail(film: FilmItem) {
        _binding.bottomSheet.let {
            it.filmDescription.text = film.description

            setWatchLaterDate(film)

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

    private fun setWatchLaterDate(filmItem: FilmItem) {
        val bottomSheet = _binding.bottomSheet
        if (filmItem.date != null) {
            bottomSheet.watchLaterDateLabel.visibility = View.VISIBLE
            bottomSheet.watchLaterDateLabel.text = getString(R.string.viewing_sheduled_for, filmItem.date)
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

                                        if (it.date == null)
                                            _detailViewModel.setDateTime(dateTime)
                                        else
                                            _detailViewModel.changeDateTime(dateTime)
                                    }

                                }).show(supportFragmentManager, "timePicker")
                            }

                        }).show(supportFragmentManager, "datePicker")
                    }

                    override fun onDeleteTimeClick() {
                        _detailViewModel.removeDateTime()
                    }
                },
                withDelete = it.date != null
            ).show(supportFragmentManager, "watchLaterDialog")
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