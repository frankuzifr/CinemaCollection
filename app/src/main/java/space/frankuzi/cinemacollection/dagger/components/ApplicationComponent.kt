package space.frankuzi.cinemacollection.dagger.components

import android.app.AlarmManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.*
import okhttp3.Headers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.App.Companion.API_KEY
import space.frankuzi.cinemacollection.data.room.AppDatabase
import javax.inject.Scope
import javax.inject.Singleton

@ApplicationScope
@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    AnalyticsModule::class,
    ServicesModule::class,
    ApplicationSubComponentsModule::class
])
interface ApplicationComponent {

    fun mainActivityComponentFactory(): MainActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}

@Module(subcomponents = [MainActivityComponent::class])
interface ApplicationSubComponentsModule

@Module
object AnalyticsModule {
    @Provides
    @ApplicationScope
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }
}

@Module
object ServicesModule {
    @Provides
    @ApplicationScope
    fun provideAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
    }
}

@Module
object DatabaseModule {
     @Provides
     @ApplicationScope
     fun provideDatabase(context: Context): AppDatabase {
         return Room.databaseBuilder(context, AppDatabase::class.java, "films_database.db")
             .build()
     }
}

@Module
object NetworkModule {
    @Provides
    @ApplicationScope
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(App.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @ApplicationScope
    fun provideOkHttpClient(): OkHttpClient {
        val headers = Headers.Builder()
            .add("X-API-KEY", API_KEY)
            .add("Content-Type", "application/json")
            .build()

        return OkHttpClient.Builder()
            .addInterceptor {
                val request = it.request()
                val newRequest = request.newBuilder()
                    .headers(headers)
                    .build()

                it.proceed(newRequest)
            }
            .build()
    }
}

@Scope
annotation class ApplicationScope