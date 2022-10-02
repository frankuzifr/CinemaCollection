package space.frankuzi.cinemacollection.watchlater.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.structs.FilmClickListener
import space.frankuzi.cinemacollection.watchlater.viewholder.WatchLaterViewHolder

class WatchLaterFilmsAdapter(
    private val listener: FilmClickListener
) : RecyclerView.Adapter<WatchLaterViewHolder>() {

    private val _items = mutableListOf<FilmItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchLaterViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return WatchLaterViewHolder(
            inflater.inflate(R.layout.watch_later_film_view_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WatchLaterViewHolder, position: Int) {
        holder.bind(_items[position], listener)
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    fun setItems(films: List<FilmItem>) {
        _items.clear()
        _items.addAll(films)
        notifyDataSetChanged()
    }

    fun changeItem(item: FilmItem) {
        val index = _items.indexOfFirst {
            it.id == item.id
        }

        if (item.date == null) {
            _items.removeAt(index)
            notifyItemRemoved(index)
        } else {
            _items[index].date = item.date
            notifyItemChanged(index)
        }
    }
}