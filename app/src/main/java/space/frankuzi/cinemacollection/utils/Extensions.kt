package space.frankuzi.cinemacollection.utils

import android.content.Context
import android.widget.Toast

private var _toast: Toast? = null

fun showToastWithText(context: Context, text: String) {
    _toast?.cancel()
    _toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
    _toast?.show()
}

fun cancelToast() {
    _toast?.cancel()
}