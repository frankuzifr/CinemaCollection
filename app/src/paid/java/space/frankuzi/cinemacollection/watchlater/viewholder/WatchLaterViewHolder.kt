package space.frankuzi.cinemacollection.watchlater.viewholder

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.structs.FilmClickListener

class WatchLaterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val _filmImage: ImageView = itemView.findViewById(R.id.film_image)
    private val _filmTitle: TextView = itemView.findViewById(R.id.film_title_label)
    private val _datetimeLabel: TextView = itemView.findViewById(R.id.dare_time_label)
    private val _filmCard: MaterialCardView = itemView.findViewById(R.id.film_card)
    private val _filmFavourite: CheckBox = itemView.findViewById(R.id.favourite_checkbox)

    fun bind(film: FilmItem, listener: FilmClickListener) {
        Glide.with(itemView)
            .load(film.imageUrl)
            .placeholder(R.drawable.ic_baseline_image_24)
            .error(R.drawable.ic_baseline_error_outline_24)
            .centerCrop()
            .into(_filmImage)

        _filmTitle.text = film.name
        _filmFavourite.isChecked = film.isFavourite

        _datetimeLabel.text = itemView.resources.getString(R.string.viewing_sheduled_for, film.getDateString())

        _filmCard.setOnClickListener {
            listener.onFilmClickListener(film, layoutPosition)
        }

        _filmFavourite.setOnClickListener {
            listener.onFilmFavouriteClickListener(film, layoutPosition)
        }
    }
}