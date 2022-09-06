package space.frankuzi.cinemacollection.mainactivityview

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.analytics.FirebaseAnalytics
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.databinding.ActivityMainBinding
import space.frankuzi.cinemacollection.details.viewmodel.DetailsViewModel
import space.frankuzi.cinemacollection.utils.broadcastreceiver.WatchLaterTimeComeInBroadcast
import space.frankuzi.cinemacollection.utils.custombackstack.CustomBackStack

class MainActivityController(
    val mainActivity: MainActivity,
    val mainBinding: ActivityMainBinding,
    val customBackStack: CustomBackStack,
    val firebaseAnalytics: FirebaseAnalytics,
    val alarmManager: AlarmManager,
    val detailViewModel: DetailsViewModel
) {

    init {
        createNotificationChannelWatchLater()
        createNotificationChannelForFirebase()
    }

    val bottomSheetHandler = BottomSheetHandler(this)
    val viewSubscribersHandler = ViewSubscribersHandler(this)
    val navigationBarHandler = NavigationBarHandler(this)

    private fun createNotificationChannelWatchLater() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mainActivity.getString(R.string.scheduled_viewing)
            val descriptionText = mainActivity.getString(R.string.schedule_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(WatchLaterTimeComeInBroadcast.WATCH_LATER_CHANNEL_ID, name, importance)
                .apply {
                    description = descriptionText
                }

            val notificationManager: NotificationManager =
                mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannelForFirebase() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = mainActivity.getString(R.string.offers)
            val descriptionText = mainActivity.getString(R.string.schedule_reminder)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(mainActivity.getString(R.string.offers), name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                mainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}