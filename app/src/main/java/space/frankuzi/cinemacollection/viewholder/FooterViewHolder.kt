package space.frankuzi.cinemacollection.viewholder

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.mainscreen.adapter.RetryLoadListener

class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val _retryButton: Button = itemView.findViewById(R.id.retry_button)
    private val _errorText: TextView = itemView.findViewById(R.id.error_text)
    private val _progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

    fun bind(listener: RetryLoadListener) {
        enableLoading()
        _retryButton.setOnClickListener{
            listener.onRetryLoadClickListener()
            enableLoading()
        }
    }

    fun enableLoading() {
        _progressBar.visibility = View.VISIBLE
        _errorText.visibility = View.INVISIBLE
        _retryButton.visibility = View.INVISIBLE
    }

    fun enableErrorLabel(errorText: String) {
        _progressBar.visibility = View.INVISIBLE
        _errorText.visibility = View.VISIBLE
        _errorText.text = errorText
        _retryButton.visibility = View.VISIBLE
    }

    fun disableAll() {
        _progressBar.visibility = View.INVISIBLE
        _errorText.visibility = View.INVISIBLE
        _retryButton.visibility = View.INVISIBLE
    }
}