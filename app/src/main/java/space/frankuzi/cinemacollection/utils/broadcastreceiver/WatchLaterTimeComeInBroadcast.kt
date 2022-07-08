package space.frankuzi.cinemacollection.utils.broadcastreceiver

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R


class WatchLaterTimeComeInBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(FILM_ID, 0)
        val filmName = intent.getStringExtra(FILM_NAME)

        val intent = Intent(context, MainActivity::class.java)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(intent)

        intent.putExtra(FILM_ID, id)

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(context, WATCH_LATER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_cinema_collection_round)
            .setContentTitle(context.getString(R.string.notification))
            .setContentText(context.getString(R.string.notification_text, filmName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(id, builder.build())
    }

    companion object {
        const val FILM_ID = "filmId"
        const val FILM_NAME = "filmName"
        const val WATCH_LATER_CHANNEL_ID = "WatchLaterChannel"
    }
}