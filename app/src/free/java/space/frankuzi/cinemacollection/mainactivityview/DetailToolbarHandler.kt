package space.frankuzi.cinemacollection.mainactivityview

import android.content.Intent
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.databinding.BottomSheetDetailBinding
import space.frankuzi.cinemacollection.utils.showToastWithText
import space.frankuzi.cinemacollection.watchlater.datetime.*

class DetailToolbarHandler(
    private val mainActivityController: MainActivityController,
    private val bottomSheetDetailBinding: BottomSheetDetailBinding
) {

    fun initToolbar(film: FilmItem) {
        bottomSheetDetailBinding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    onShareButtonClick(film.name)
                }
                R.id.watch_later -> {
                    openDateTimePicker()
                }
            }
            true
        }
    }

    private fun onShareButtonClick(filmName: String?) {

        val sendMessageIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mainActivityController.mainActivity.getString(R.string.invite_message, filmName))
        }

        val sharedIntent = Intent.createChooser(sendMessageIntent, null)

        mainActivityController.mainActivity.startActivity(sharedIntent)
    }

    private fun openDateTimePicker() {

        val filmItem = mainActivityController.detailViewModel.selectedItem.value

        filmItem?.let {
            WatchLaterDialog(
                title = if (it.date == null)
                    mainActivityController.mainActivity.getString(R.string.no_viewing_time)
                else
                    mainActivityController.mainActivity.getString(R.string.viewing_sheduled_for, filmItem.getDateString()),
                watchLaterListener = object : WatchLaterListener {
                    override fun onChangeTimeClick() {
                        DatePickerFragment(object : DateSelectHandler {
                            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
                                TimePickerFragment(object : TimeSelectHandler {
                                    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
                                        val dateTime = DateTime(
                                            dayOfMonth = dayOfMonth,
                                            month = month,
                                            year = year,
                                            hour = hourOfDay,
                                            minute = minute
                                        )

                                        if (it.date == null) {
                                            mainActivityController.detailViewModel.setDateTime(dateTime)
                                            it.date = dateTime.getDate()
                                            showToastWithText(
                                                mainActivityController.mainActivity,
                                                mainActivityController.mainActivity.getString(R.string.film_added_to_sheduker_viewing,
                                                    filmItem.name,
                                                    filmItem.getDateString()
                                                ))
                                        } else {
                                            mainActivityController.detailViewModel.changeDateTime(dateTime)
                                            it.date = dateTime.getDate()
                                            showToastWithText(
                                                mainActivityController.mainActivity,
                                                mainActivityController.mainActivity.getString(R.string.changes_time_sheduler_viewing,
                                                    filmItem.name,
                                                    filmItem.getDateString()
                                                ))
                                        }
                                    }

                                }).show(mainActivityController.mainActivity.supportFragmentManager, "timePicker")
                            }

                        }).show(mainActivityController.mainActivity.supportFragmentManager, "datePicker")
                    }

                    override fun onDeleteTimeClick() {
                        mainActivityController.detailViewModel.removeDateTime()
                        showToastWithText(
                            mainActivityController.mainActivity,
                            mainActivityController.mainActivity.getString(R.string.film_remove_from_watch_later, filmItem.name))
                    }
                },
                withDelete = it.date != null
            ).show(mainActivityController.mainActivity.supportFragmentManager, "watchLaterDialog")
        }
    }

}