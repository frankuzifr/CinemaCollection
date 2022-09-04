package space.frankuzi.cinemacollection.mainScreen.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.stubs.FilmsApiStub
import space.frankuzi.cinemacollection.utils.LoadIdlingResource

@RunWith(JUnit4::class)
class MainViewModelTest : TestCase() {
    private lateinit var database: AppDatabase
    private lateinit var filmsApi: FilmsApi
    private lateinit var viewModel: MainViewModel

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        filmsApi = FilmsApiStub()
        viewModel = MainViewModel(filmsApi, database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun successLoadFilmsTest() = runBlocking {
        viewModel.loadFilms()
        Thread.sleep(100)
        val value = viewModel.films.value
        assert(value!!.isNotEmpty())
        assert(viewModel.loadError.value == null)
    }

    @Test
    fun errorLoadFilmsTest() = runBlocking {
        val filmsApiStub = FilmsApiStub()
        filmsApiStub.errorSimulate = true
        val viewModel = MainViewModel(filmsApiStub, database)
        viewModel.loadFilms()
        Thread.sleep(100)
        assert(viewModel.films.value == null)
        assert(viewModel.loadError.value != null)
    }

    @Test
    fun searchFilmsBySymbolTest() = runBlocking {
        database.getFilmsDao().addFilms(
            listOf(
                FilmDbEntity(
                    id = 0,
                    kinopoiskId = 444,
                    nameOriginal = "Tat",
                    nameRussian = "Тат",
                    description = "Some",
                    imageUrl = "https://",
                    type = null
                ),
                FilmDbEntity(
                    id = 0,
                    kinopoiskId = 434,
                    nameOriginal = "Otg",
                    nameRussian = "Отг",
                    description = "Some",
                    imageUrl = "https://",
                    type = null
                ),
                FilmDbEntity(
                    id = 0,
                    kinopoiskId = 424,
                    nameOriginal = "Oao",
                    nameRussian = "Оао",
                    description = "Some",
                    imageUrl = "https://",
                    type = null
                )
            )
        )

        viewModel.searchFilmsByName("t")
        Thread.sleep(1000)
        assert(viewModel.films.value!!.size == 2)
        assert(viewModel.loadError.value == null)
    }
}