package space.frankuzi.cinemacollection.mainactivityview

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

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
    }
}