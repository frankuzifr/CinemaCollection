package space.frankuzi.cinemacollection.viewholder

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

    fun bind(film: FilmItem, mainActivity: MainActivity, listener: FilmClickListener) {
        _filmImage.setBackgroundResource(film.imageIdRes)
        _filmTitle.setText(film.nameIdRes)
        _filmTitle.setTextColor(mainActivity.resources.getColor(film.titleColorId))
        _filmFavourite.isChecked = film.isFavourite

        _filmCard.setOnClickListener {
            film.isSelected = true
            listener.onFilmClickListener(film, layoutPosition)

            val intent = Intent(mainActivity, FilmDetailActivity::class.java)
            intent.putExtra(FilmDetailActivity.FILM_ID, layoutPosition)
            intent.putExtra(FilmDetailActivity.IMAGE_ID_RES, film.imageIdRes)
            intent.putExtra(FilmDetailActivity.NAME_ID_RES, film.nameIdRes)
            intent.putExtra(FilmDetailActivity.DESCRIPTION_ID_RES, film.descriptionIdRes)
            intent.putExtra(FilmDetailActivity.IS_FAVOURITE, film.isFavourite)
            mainActivity.startActivityForResult(intent, mainActivity.requestCode)
        }

        _filmFavourite.setOnClickListener {
            val filmName = mainActivity.resources.getString(film.nameIdRes)
            if (film.isFavourite) {
                film.isFavourite = false
                showToastWithText(mainActivity, mainActivity.resources.getString(R.string.film_removed_from_favourites, filmName))
            } else {
                film.isFavourite = true
                showToastWithText(mainActivity, mainActivity.resources.getString(R.string.film_added_to_favourites, filmName))
            }
        }
    }
}