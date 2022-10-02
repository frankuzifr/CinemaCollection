package space.frankuzi.cinemacollection.watchlater.datetime

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var watchLaterListener: WatchLaterListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            year = savedInstanceState.getInt(DatePickerFragment.YEAR)
            month = savedInstanceState.getInt(DatePickerFragment.MONTH)
            day = savedInstanceState.getInt(DatePickerFragment.DAY)
            watchLaterListener = savedInstanceState.getParcelable(WatchLaterDialog.WATCH_LATER_LISTENER)
        } else {
            arguments?.let {
                year = it.getInt(DatePickerFragment.YEAR)
                month = it.getInt(DatePickerFragment.MONTH)
                day = it.getInt(DatePickerFragment.DAY)
                watchLaterListener = it.getParcelable(WatchLaterDialog.WATCH_LATER_LISTENER)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        watchLaterListener?.onChangeTimeClick(
            DateTime(
                dayOfMonth = day,
                month = month,
                year = year,
                minute = minute,
                hour = hourOfDay
            )
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(DatePickerFragment.YEAR, year)
        outState.putInt(DatePickerFragment.MONTH, month)
        outState.putInt(DatePickerFragment.DAY, day)
        outState.putParcelable(WatchLaterDialog.WATCH_LATER_LISTENER, watchLaterListener)

        super.onSaveInstanceState(outState)
    }
}