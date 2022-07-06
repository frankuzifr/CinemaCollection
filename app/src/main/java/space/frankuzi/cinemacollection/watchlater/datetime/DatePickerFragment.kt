package space.frankuzi.cinemacollection.watchlater.datetime

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(
    private val dateSelectHandler: DateSelectHandler
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

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
        dateSelectHandler.onDateSelected(dayOfMonth, month, year)
    }
}

interface DateSelectHandler {
    fun onDateSelected(dayOfMonth: Int, month: Int, year: Int)
}