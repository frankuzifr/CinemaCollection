package space.frankuzi.cinemacollection

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ExitDialog(
    private val confirmListener: () -> Unit
): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.you_want_exit))
            .setNegativeButton(getString(R.string.close_dialog)) { dialog, _ ->
                onCancel(dialog)
            }
            .setPositiveButton(getString(R.string.exit)) { _, _ ->
                confirmListener()
            }
            .create()
    }
}