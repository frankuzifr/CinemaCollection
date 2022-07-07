package space.frankuzi.cinemacollection.watchlater.datetime

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import space.frankuzi.cinemacollection.R

class WatchLaterDialog(
    private val title: String,
    private val watchLaterListener: WatchLaterListener,
    private val withDelete: Boolean
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setNeutralButton(getString(R.string.close_dialog)) { dialog, _ ->
            onCancel(dialog)
        }
        builder.setPositiveButton(getString(R.string.change)) { dialog, _ ->
            watchLaterListener.onChangeTimeClick()
            onCancel(dialog)
        }
        if (withDelete) {
            builder.setNegativeButton(getString(R.string.delete)) { dialog, _ ->
                watchLaterListener.onDeleteTimeClick()
                onCancel(dialog)
            }
        }

        return builder.create()
    }
}

interface WatchLaterListener {
    fun onChangeTimeClick()

    fun onDeleteTimeClick()
}