package space.frankuzi.cinemacollection.watchlater.datetime

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import space.frankuzi.cinemacollection.R

class WatchLaterDialog : DialogFragment() {

    private var title: String? = null
    private var watchLaterListener: WatchLaterListener? = null
    private var withDelete: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TITLE)
            watchLaterListener = savedInstanceState.getParcelable(WATCH_LATER_LISTENER)
            withDelete = savedInstanceState.getBoolean(WITH_DELETE)
        } else {
            arguments?.let {
                title = it.getString(TITLE)
                watchLaterListener = it.getParcelable(WATCH_LATER_LISTENER)
                withDelete = it.getBoolean(WITH_DELETE)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setNeutralButton(getString(R.string.close_dialog)) { dialog, _ ->
            onCancel(dialog)
        }
        builder.setPositiveButton(getString(R.string.change)) { dialog, _ ->
            val datePickerFragment = DatePickerFragment()
            val bundle = Bundle()
            bundle.putParcelable(WATCH_LATER_LISTENER, watchLaterListener)
            datePickerFragment.arguments = bundle
            datePickerFragment.show(parentFragmentManager, "dateTimePicker")
            //watchLaterListener?.onChangeTimeClick()
            onCancel(dialog)
        }
        if (withDelete) {
            builder.setNegativeButton(getString(R.string.delete)) { dialog, _ ->
                watchLaterListener?.onDeleteTimeClick()
                onCancel(dialog)
            }
        }

        return builder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TITLE, title)
        outState.putParcelable(WATCH_LATER_LISTENER, watchLaterListener)
        outState.putBoolean(WITH_DELETE, withDelete)

        super.onSaveInstanceState(outState)
    }

    companion object {
        const val TITLE = "title"
        const val WATCH_LATER_LISTENER = "watchLaterListener"
        const val WITH_DELETE = "withDelete"
    }
}

interface WatchLaterListener : Parcelable {
    fun onChangeTimeClick(dateTime: DateTime)
    fun onDeleteTimeClick()
}