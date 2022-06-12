package space.frankuzi.cinemacollection

import android.app.Application
import android.util.Log
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.frankuzi.cinemacollection.network.FilmsApi


class App : Application() {

    lateinit var filmsApi: FilmsApi

    override fun onCreate() {
        super.onCreate()

        instance = this

        val headers = Headers.Builder()
            .add("X-API-KEY", API_KEY)
            .add("Content-Type", "application/json")
            .build()

        val client = OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request()
                val newRequest = request.newBuilder()
                    .headers(headers)
                    .build()

                it.proceed(newRequest)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        Log.i("APP", "APP")
        filmsApi = retrofit.create(FilmsApi::class.java)
    }

    companion object{
        const val BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.2/"
        const val API_KEY = "16683e04-b3a2-4493-8117-4556fa7220f3"

        lateinit var instance: App
        private set
    }
}
