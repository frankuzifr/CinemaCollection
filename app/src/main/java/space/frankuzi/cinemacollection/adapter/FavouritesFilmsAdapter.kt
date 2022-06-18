package space.frankuzi.cinemacollection.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.viewholder.FilmItemViewHolder

class FavouritesFilmsAdapter(
    private val listener: FilmClickListener
) : RecyclerView.Adapter<FilmItemViewHolder>() {

    private val _items = mutableListOf<FilmItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FilmItemViewHolder(
            inflater.inflate(R.layout.film_view_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmItemViewHolder, position: Int) {
        holder.bind(_items[position], listener)
    }

    override fun getItemCount(): Int {
        return _items.size
    }

    fun setItems(items: List<FilmItem>) {
        _items.clear()
        _items.addAll(items)

        notifyDataSetChanged()
    }

    fun removeItem(filmItem: FilmItem) {
        val index = _items.indexOfFirst {
            it.id == filmItem.id
        }
        _items.removeAt(index)
        notifyItemRemoved(index)
    }

    fun addItem(filmItem: FilmItem, index: Int) {
        _items.add(index, filmItem)
        notifyItemInserted(index)
    }
}