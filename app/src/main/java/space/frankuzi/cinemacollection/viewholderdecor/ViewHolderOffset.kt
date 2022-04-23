package space.frankuzi.cinemacollection.viewholderdecor

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class ViewHolderOffset(
    private val spacing: Int,

) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spacing
        outRect.right = spacing
        outRect.bottom = spacing
        outRect.top = spacing
    }
}