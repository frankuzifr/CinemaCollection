package space.frankuzi.cinemacollection.viewholder.viewholderanim

import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import space.frankuzi.cinemacollection.R

class CustomItemAnimator() : DefaultItemAnimator() {
    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {

        holder?.itemView?.animation = AnimationUtils.loadAnimation(
            holder?.itemView?.context,
            R.anim.viewholder_add_anim
        )
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {

        holder?.itemView?.animation = AnimationUtils.loadAnimation(
            holder?.itemView?.context,
            R.anim.viewholder_remove_anim
        )
        return super.animateRemove(holder)
    }
}