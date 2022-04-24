package space.frankuzi.cinemacollection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.viewholder.FilmItemViewHolder

class FilmItemAdapter(
    private val items: List<FilmItem>,
    private val activity: Activity,
    private val listener: FilmClickListener
) : RecyclerView.Adapter<FilmItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return FilmItemViewHolder(
            inflater.inflate(R.layout.film_view_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmItemViewHolder, position: Int) {
        holder.bind(items[position], activity, listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

interface FilmClickListener
{
    fun onFilmClickListener(film: FilmItem, position: Int)
    fun onFilmFavouriteClickListener(film: FilmItem, position: Int)
}