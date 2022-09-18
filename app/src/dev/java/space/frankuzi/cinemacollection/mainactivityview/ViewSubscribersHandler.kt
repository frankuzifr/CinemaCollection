package space.frankuzi.cinemacollection.mainactivityview

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.resources.Compatibility.Api21Impl.inflate
import androidx.core.content.res.ColorStateListInflaterCompat.inflate
import androidx.core.graphics.drawable.DrawableCompat.inflate
import com.bumptech.glide.Glide
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.FragmentMainBinding.inflate
import space.frankuzi.cinemacollection.databinding.FragmentWatchLaterBinding.inflate
import space.frankuzi.cinemacollection.utils.broadcastreceiver.WatchLaterTimeComeInBroadcast
import kotlin.math.log

class ViewSubscribersHandler(
    private val mainActivityController: MainActivityController
) {

    init {
        initSubscribers()
    }

    private fun initSubscribers() {

        mainActivityController.detailViewModel.watchLaterChanged.observe(mainActivityController.mainActivity) { filmItem ->
            setWatchLaterDate(filmItem)

            val intent = Intent(filmItem.id.toString(), null, mainActivityController.mainActivity, WatchLaterTimeComeInBroadcast::class.java)
            intent.putExtra(WatchLaterTimeComeInBroadcast.FILM_ID, filmItem.id)
            intent.putExtra(WatchLaterTimeComeInBroadcast.FILM_NAME, filmItem.name)

            val pendingIntent = PendingIntent.getBroadcast(mainActivityController.mainActivity, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            mainActivityController.alarmManager.cancel(pendingIntent)

            filmItem.date?.let {
                mainActivityController.alarmManager.set(AlarmManager.RTC_WAKEUP, it.time, pendingIntent)
            }
        }

        mainActivityController.detailViewModel.selectedItem.observe(mainActivityController.mainActivity) { filmItem ->
            filmItem?.let { film ->
                openFilmDetail(film)
            }
        }

        mainActivityController.detailViewModel.descriptionLoaded.observe(mainActivityController.mainActivity) { description->
            setDescription(description)
        }

        mainActivityController.detailViewModel.loadError.observe(mainActivityController.mainActivity) {
            mainActivityController.mainBinding.bottomSheet.loadedStatus.progressBar.visibility = View.INVISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.errorText.visibility = View.VISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.errorText.text = it
            mainActivityController.mainBinding.bottomSheet.loadedStatus.retryButton.visibility = View.VISIBLE
        }

        mainActivityController.detailViewModel.filmNotes.observe(mainActivityController.mainActivity) { notes ->
            val bottomSheet = mainActivityController.mainBinding.bottomSheet
            val layoutInflater = LayoutInflater.from(mainActivityController.mainActivity)

            for (note in notes) {
                val noteLayout = layoutInflater.inflate(R.layout.note_layout, null)
                val dateLabel = noteLayout.findViewById<TextView>(R.id.date_label)
                val noteLabel = noteLayout.findViewById<TextView>(R.id.note_label)
                dateLabel.text = note.date.toString()
                noteLabel.text = note.note
                bottomSheet.notesContainer.addView(noteLayout)
            }
        }
    }

    private fun setWatchLaterDate(filmItem: FilmItem) {
        val bottomSheet = mainActivityController.mainBinding.bottomSheet
        if (filmItem.date != null) {
            bottomSheet.watchLaterDateLabel.visibility = View.VISIBLE
            bottomSheet.watchLaterDateLabel.text = mainActivityController.mainActivity.getString(R.string.viewing_sheduled_for, filmItem.getDateString())
        } else {
            bottomSheet.watchLaterDateLabel.visibility = View.INVISIBLE
        }
    }

    private fun openFilmDetail(film: FilmItem) {
        mainActivityController.mainBinding.bottomSheet.let {
            it.filmDescription.visibility = View.INVISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.progressBar.visibility = View.VISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE

            setWatchLaterDate(film)

            it.toolbar.title = film.name
            it.collapsingToolbar.title = film.name
            Glide.with(mainActivityController.mainActivity)
                .load(film.imageUrl)
                .placeholder(R.drawable.ic_baseline_image_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                .centerCrop()
                .into(it.filmImage)

            DetailToolbarHandler(mainActivityController, it).initToolbar(film)
        }

        mainActivityController.bottomSheetHandler.showDetail()

    }

    private fun setDescription(description: String?) {
        if (description == null)
            return

        mainActivityController.mainBinding.bottomSheet.filmDescription.visibility = View.VISIBLE
        mainActivityController.mainBinding.bottomSheet.filmDescription.text = description
        mainActivityController.mainBinding.bottomSheet.loadedStatus.progressBar.visibility = View.INVISIBLE
        mainActivityController.mainBinding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
        mainActivityController.mainBinding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE
    }
}