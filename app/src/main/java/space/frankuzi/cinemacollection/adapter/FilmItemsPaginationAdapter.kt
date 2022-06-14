package space.frankuzi.cinemacollection.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.viewholder.FilmItemViewHolder
import space.frankuzi.cinemacollection.viewholder.FooterViewHolder

class FilmItemsPaginationAdapter(
    private val filmClickListener: FilmClickListener,
    private val retryLoadClickListener: RetryLoadListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var _footerViewHolder: FooterViewHolder
    private val _items = mutableListOf<FilmItem>()
    var isLastPages = false
        set(value) {
            field = value
            if (value)
                _footerViewHolder.disableAll()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_ITEMS) {
            FilmItemViewHolder(
                inflater.inflate(R.layout.film_view_card, parent, false)
            )
        }
        else {
            _footerViewHolder = FooterViewHolder(
                inflater.inflate(R.layout.recycler_view_footer, parent, false)
            )

            _footerViewHolder
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEMS
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position >= _items.size;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder){
            is FilmItemViewHolder -> {
                holder.bind(_items[position], filmClickListener)
            }
            is FooterViewHolder -> {
                holder.bind(retryLoadClickListener)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLastPages) _items.size else _items.size + 1
    }

    fun setItems(items: List<FilmItem>) {
        _items.clear()
        _items.addAll(items)

        notifyDataSetChanged()
    }

    fun setError(errorMessage: String) {
        _footerViewHolder.enableErrorLabel(errorMessage)
    }

    fun setLoading() {
        _footerViewHolder.enableLoading()
    }

    companion object {
        const val TYPE_ITEMS = 0
        const val TYPE_FOOTER = 1
    }
}

interface FilmClickListener {
    fun onFilmClickListener(film: FilmItem, position: Int)
    fun onFilmFavouriteClickListener(film: FilmItem, position: Int)
}

interface RetryLoadListener {
    fun onRetryLoadClickListener()
}