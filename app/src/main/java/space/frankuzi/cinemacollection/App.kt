package space.frankuzi.cinemacollection

import android.app.Application
import space.frankuzi.cinemacollection.dagger.components.ApplicationComponent
import space.frankuzi.cinemacollection.dagger.components.DaggerApplicationComponent
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase

class App : Application() {

//    lateinit var filmsApi: FilmsApi
//    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()

        _applicationComponentInstance = DaggerApplicationComponent.factory().create(this)
    }

    companion object{
        const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.2/"
        const val API_KEY = "16683e04-b3a2-4493-8117-4556fa7220f3"

        var _applicationComponentInstance: ApplicationComponent? = null
            private set
    }
}
