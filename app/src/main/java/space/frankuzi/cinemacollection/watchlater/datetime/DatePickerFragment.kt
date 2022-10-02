package space.frankuzi.cinemacollection.watchlater.datetime

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var watchLaterListener: WatchLaterListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            watchLaterListener = savedInstanceState.getParcelable(WatchLaterDialog.WATCH_LATER_LISTENER)
        } else {
            arguments?.let {
                watchLaterListener = it.getParcelable(WatchLaterDialog.WATCH_LATER_LISTENER)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(requireContext(), this, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;

        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val timePickerFragment = TimePickerFragment()
        val bundle = Bundle()
        bundle.putInt(YEAR, year)
        bundle.putInt(MONTH, month)
        bundle.putInt(DAY, dayOfMonth)
        bundle.putParcelable(WatchLaterDialog.WATCH_LATER_LISTENER, watchLaterListener)
        timePickerFragment.arguments = bundle
        timePickerFragment.show(parentFragmentManager, "timePickerDialog")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(WatchLaterDialog.WATCH_LATER_LISTENER, watchLaterListener)

        super.onSaveInstanceState(outState)
    }

    companion object {
        const val YEAR = "year"
        const val MONTH = "month"
        const val DAY = "day"
    }
}