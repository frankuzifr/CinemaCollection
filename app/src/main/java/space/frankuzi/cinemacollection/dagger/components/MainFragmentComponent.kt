package space.frankuzi.cinemacollection.dagger.components

import dagger.Subcomponent
import space.frankuzi.cinemacollection.mainscreen.view.FragmentMain

@Subcomponent
interface MainFragmentComponent {

    fun inject(mainFragment: FragmentMain)

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainFragmentComponent
    }
}