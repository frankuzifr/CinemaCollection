package space.frankuzi.cinemacollection.mainScreen.model

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.errorprone.annotations.DoNotMock
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.stubs.FilmsApiStub

@RunWith(JUnit4::class)
class MainRepositoryTest : TestCase() {

    private lateinit var database: AppDatabase
    private lateinit var filmsApi: FilmsApi
    private lateinit var mainRepository: MainRepository

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        filmsApi = FilmsApiStub()
        mainRepository = MainRepository(filmsApi, database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun loadFilmsWhenDbEmptyTest() = runBlocking {
        val films = mainRepository.loadFilms()
        assert(films.isNotEmpty())
    }

    @Test
    fun loadFilmsWhenDbNotEmpty() = runBlocking {
        database.getFilmsDao().addFilm(FilmDbEntity(
            id = 0,
            kinopoiskId = 444,
            nameOriginal = "Tat",
            nameRussian = "Тат",
            description = "Some",
            imageUrl = "https://",
            type = null
        ))

        val films = mainRepository.searchFilmsByName("t")
        assert(films.isNotEmpty())
    }

    @Test
    fun getFilmByNameTest() = runBlocking {
        database.getFilmsDao().addFilm(FilmDbEntity(
            id = 0,
            kinopoiskId = 444,
            nameOriginal = "Tat",
            nameRussian = "Тат",
            description = "Some",
            imageUrl = "https://",
            type = null
        ))

        val films = mainRepository.searchFilmsByName("t")
        assert(films.isNotEmpty())
    }
}