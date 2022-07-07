package space.frankuzi.cinemacollection.utils.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R


class WatchLaterTimeComeInBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("filmId", 0)
        val filmName = intent.getStringExtra("filmName")

        val intent = Intent(context, MainActivity::class.java)

        intent.putExtra("filmId", id)

        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.mipmap.ic_launcher_cinema_collection_round)
            .setContentTitle("Напоминание")
            .setContentText("Вы запланировали просмотр $filmName")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(id, builder.build())
    }
}