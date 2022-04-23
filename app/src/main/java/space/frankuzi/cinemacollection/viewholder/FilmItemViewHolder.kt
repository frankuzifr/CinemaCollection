package space.frankuzi.cinemacollection.viewholder

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.FilmDetailActivity
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import com.google.android.material.card.MaterialCardView as MaterialCard

class FilmItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val _filmImage: ImageView = itemView.findViewById(R.id.film_image)
    private val _filmTitle: TextView = itemView.findViewById(R.id.film_title_label)
    private val _filmCard: MaterialCard = itemView.findViewById(R.id.film_card)

    fun bind(film: FilmItem, mainActivity: MainActivity) {
        _filmImage.setBackgroundResource(film.imageIdRes)
        _filmTitle.setText(film.nameIdRes)
        _filmTitle.setTextColor(mainActivity.resources.getColor(film.titleColorId))

        _filmCard.setOnClickListener {
            film.isSelected = true
            _filmTitle.setTextColor(mainActivity.resources.getColor(film.titleColorId))
            Log.i("", layoutPosition.toString())

            val intent = Intent(mainActivity, FilmDetailActivity::class.java)
            intent.putExtra(FilmDetailActivity.FILM_ID, layoutPosition)
            intent.putExtra(FilmDetailActivity.IMAGE_ID_RES, film.imageIdRes)
            intent.putExtra(FilmDetailActivity.NAME_ID_RES, film.nameIdRes)
            intent.putExtra(FilmDetailActivity.DESCRIPTION_ID_RES, film.descriptionIdRes)
            intent.putExtra(FilmDetailActivity.IS_FAVOURITE, film.isFavourite)
            mainActivity.startActivityForResult(intent, mainActivity.requestCode)
        }
    }
}