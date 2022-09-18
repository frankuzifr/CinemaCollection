package space.frankuzi.cinemacollection.mainactivityview

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.structs.FilmNote
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class BottomSheetHandler(
    private val mainActivityController: MainActivityController
) {

    init {
        initBottomSheet()
    }

    lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    private fun initBottomSheet() {
        val bottomSheet = mainActivityController.mainBinding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet.designBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheet.toolbar.setNavigationOnClickListener {
            closeDetail()
        }

        bottomSheet.loadedStatus.retryButton.setOnClickListener {
            mainActivityController.mainBinding.bottomSheet.loadedStatus.progressBar.visibility = View.VISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.errorText.visibility = View.INVISIBLE
            mainActivityController.mainBinding.bottomSheet.loadedStatus.retryButton.visibility = View.INVISIBLE

            mainActivityController.detailViewModel.loadDescription()
        }

        bottomSheet.leaveNoteButton.setOnClickListener {
            val text = bottomSheet.comment.editText?.text.toString()
            bottomSheet.comment.editText?.text?.clear()

            val format = LocalDate.now().toString()
            val filmNote = FilmNote(
                date = format,
                note = text
            )

            mainActivityController.detailViewModel.addFilmNote(filmNote)

            val layoutInflater = LayoutInflater.from(mainActivityController.mainActivity)
            val inflate = layoutInflater.inflate(R.layout.note_layout, null)

            val dateLabel = inflate.findViewById<TextView>(R.id.date_label)
            val noteLabel = inflate.findViewById<TextView>(R.id.note_label)

            dateLabel.text = format
            noteLabel.text = text

            mainActivityController.mainBinding.bottomSheet.notesContainer.addView(inflate)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        mainActivityController.detailViewModel.closeDetail()
                        mainActivityController.mainBinding.bottomSheet.appBar.setExpanded(true)
                        mainActivityController.mainBinding.bottomSheet.filmDescription.text = ""
                    }
                    else -> {

                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })
    }

    fun showDetail() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun closeDetail() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mainActivityController.mainBinding.bottomSheet.notesContainer.removeAllViews()
    }
}