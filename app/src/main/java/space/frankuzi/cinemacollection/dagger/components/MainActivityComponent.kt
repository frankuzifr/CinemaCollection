package space.frankuzi.cinemacollection.dagger.components

import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import retrofit2.Retrofit
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.data.network.FilmsApi
import javax.inject.Scope

@MainActivityScope
@Subcomponent(modules = [
    FilmsApiModule::class,
    MainActivitySubComponentsModule::class
])
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun mainFragmentComponentFactory(): MainFragmentComponent.Factory
    fun watchLaterComponentFactory(): WatchLaterFragmentComponent.Factory
    fun favouritesComponentFactory(): FavouritesFragmentComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainActivityComponent
    }
}

@Module(subcomponents = [
    MainFragmentComponent::class,
    WatchLaterFragmentComponent::class,
    FavouritesFragmentComponent::class
])
interface MainActivitySubComponentsModule

@Module
object FilmsApiModule {
    @Provides
    fun provideFilmsApi(retrofit: Retrofit): FilmsApi {
        return retrofit.create(FilmsApi::class.java)
    }
}

@Scope
annotation class MainActivityScope
