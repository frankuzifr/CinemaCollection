package space.frankuzi.cinemacollection.dagger.components

import dagger.Subcomponent
import space.frankuzi.cinemacollection.favouritesScreen.view.FragmentFavourites

@Subcomponent
interface FavouritesFragmentComponent {

    fun inject(favourites: FragmentFavourites)

    @Subcomponent.Factory
    interface Factory {
        fun create(): FavouritesFragmentComponent
    }
}