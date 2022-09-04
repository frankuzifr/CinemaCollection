package space.frankuzi.cinemacollection

import android.app.Application
import space.frankuzi.cinemacollection.dagger.components.ApplicationComponent
import space.frankuzi.cinemacollection.dagger.components.DaggerApplicationComponent

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        applicationComponentInstance = DaggerApplicationComponent.factory().create(this)
    }

    companion object{
        const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.2/"
        const val API_KEY = "16683e04-b3a2-4493-8117-4556fa7220f3"

        var applicationComponentInstance: ApplicationComponent? = null
            private set
    }
}
