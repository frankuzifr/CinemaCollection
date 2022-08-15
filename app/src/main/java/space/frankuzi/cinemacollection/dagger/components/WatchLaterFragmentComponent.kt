package space.frankuzi.cinemacollection.dagger.components

import dagger.Subcomponent
import space.frankuzi.cinemacollection.watchlater.view.FragmentWatchLater

@Subcomponent
interface WatchLaterFragmentComponent {

    fun inject(watchLater: FragmentWatchLater)

    @Subcomponent.Factory
    interface Factory {
        fun create(): WatchLaterFragmentComponent
    }

}