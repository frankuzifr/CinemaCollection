package space.frankuzi.cinemacollection.mainactivityview

import android.content.Intent
import android.os.Bundle
import android.os.Parcel
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
        setFavouriteState()

        bottomSheetDetailBinding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.share -> {
                    onShareButtonClick(film.name)
                }
                R.id.favourite -> {

                    mainActivityController.detailViewModel.onClickFavourite(film)

                    val filmName = film.name
                    showToastWithText(
                        mainActivityController.mainActivity,
                        if (!menuItem.isChecked)
                            mainActivityController.mainActivity.getString(
                                R.string.film_added_to_favourites,
                                filmName)
                        else
                            mainActivityController.mainActivity.getString(
                                R.string.film_removed_from_favourites,
                                filmName)
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

    private fun setFavouriteState() {
        val favouriteItem = mainActivityController.mainBinding.bottomSheet.toolbar.menu.getItem(0)

        mainActivityController.detailViewModel.selectedItem.value?.let {
            favouriteItem.isChecked = it.isFavourite
            favouriteItem.setIcon(it.favouriteIconId)
            favouriteItem.setTitle(it.favouriteLabelId)
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
            val bundle = Bundle()
            bundle.putString(
                WatchLaterDialog.TITLE,
                if (it.date == null)
                    mainActivityController.mainActivity.getString(R.string.no_viewing_time)
                else
                    mainActivityController.mainActivity.getString(R.string.viewing_sheduled_for, filmItem.getDateString())
            )
            bundle.putParcelable(
                WatchLaterDialog.WATCH_LATER_LISTENER,
                object : WatchLaterListener {
                    override fun onChangeTimeClick(dateTime: DateTime) {
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

                    override fun onDeleteTimeClick() {
                        mainActivityController.detailViewModel.removeDateTime()
                        showToastWithText(
                            mainActivityController.mainActivity,
                            mainActivityController.mainActivity.getString(R.string.film_remove_from_watch_later, filmItem.name)
                        )
                    }

                    override fun describeContents(): Int {
                        return 0
                    }

                    override fun writeToParcel(dest: Parcel?, flags: Int) {

                    }

                }
                )

            val dialog = WatchLaterDialog()
            dialog.arguments = bundle
            dialog.show(mainActivityController.mainActivity.supportFragmentManager, "watchLaterDialog")
//                dialog.title = if (it.date == null)
//                    mainActivityController.mainActivity.getString(R.string.no_viewing_time)
//                else
//                    mainActivityController.mainActivity.getString(R.string.viewing_sheduled_for, filmItem.getDateString())
//                dialog.watchLaterListener = object : WatchLaterListener {
//                    override fun onChangeTimeClick() {
//                        DatePickerFragment(object : DateSelectHandler {
//                            override fun onDateSelected(dayOfMonth: Int, month: Int, year: Int) {
//                                TimePickerFragment(object : TimeSelectHandler {
//                                    override fun onTimeSelected(hourOfDay: Int, minute: Int) {
//                                        val dateTime = DateTime(
//                                            dayOfMonth = dayOfMonth,
//                                            month = month,
//                                            year = year,
//                                            hour = hourOfDay,
//                                            minute = minute
//                                        )
//
//                                        if (it.date == null) {
//                                            mainActivityController.detailViewModel.setDateTime(dateTime)
//                                            it.date = dateTime.getDate()
//                                            showToastWithText(
//                                                mainActivityController.mainActivity,
//                                                mainActivityController.mainActivity.getString(R.string.film_added_to_sheduker_viewing,
//                                                    filmItem.name,
//                                                    filmItem.getDateString()
//                                                ))
//                                        } else {
//                                            mainActivityController.detailViewModel.changeDateTime(dateTime)
//                                            it.date = dateTime.getDate()
//                                            showToastWithText(
//                                                mainActivityController.mainActivity,
//                                                mainActivityController.mainActivity.getString(R.string.changes_time_sheduler_viewing,
//                                                    filmItem.name,
//                                                    filmItem.getDateString()
//                                                ))
//                                        }
//                                    }
//
//                                }).show(mainActivityController.mainActivity.supportFragmentManager, "timePicker")
//                            }
//
//                        }).show(mainActivityController.mainActivity.supportFragmentManager, "datePicker")
//                    }
//
//                    override fun onDeleteTimeClick() {
//                        mainActivityController.detailViewModel.removeDateTime()
//                        showToastWithText(
//                            mainActivityController.mainActivity,
//                            mainActivityController.mainActivity.getString(R.string.film_remove_from_watch_later, filmItem.name)
//                        )
//                    }
//                }
//                dialog.withDelete = it.date != null
//            dialog.show(mainActivityController.mainActivity.supportFragmentManager, "watchLaterDialog")
        }
    }

}