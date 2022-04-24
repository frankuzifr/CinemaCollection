package space.frankuzi.cinemacollection.viewholder

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.FilmDetailActivity
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.adapter.FilmClickListener
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.showToastWithText
import com.google.android.material.card.MaterialCardView as MaterialCard

class FilmItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val _filmImage: ImageView = itemView.findViewById(R.id.film_image)
    private val _filmTitle: TextView = itemView.findViewById(R.id.film_title_label)
    private val _filmCard: MaterialCard = itemView.findViewById(R.id.film_card)
    private val _filmFavourite: CheckBox = itemView.findViewById(R.id.favourite_checkbox)

    fun bind(film: FilmItem, activity: Activity, listener: FilmClickListener) {
        _filmImage.setBackgroundResource(film.imageIdRes)
        _filmTitle.setText(film.nameIdRes)
        _filmTitle.setTextColor(activity.resources.getColor(film.titleColorId))
        _filmFavourite.isChecked = film.isFavourite

        _filmCard.setOnClickListener {
            listener.onFilmClickListener(film, layoutPosition)
        }

        _filmFavourite.setOnClickListener {
            listener.onFilmFavouriteClickListener(film, layoutPosition)
        }
    }
}