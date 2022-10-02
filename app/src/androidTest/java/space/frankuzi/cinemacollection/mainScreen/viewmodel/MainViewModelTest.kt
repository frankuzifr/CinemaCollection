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
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity
import space.frankuzi.cinemacollection.mainscreen.viewmodel.ErrorMessage
import space.frankuzi.cinemacollection.mainscreen.viewmodel.MainViewModel
import space.frankuzi.cinemacollection.stubs.FilmsApiStub
import space.frankuzi.cinemacollection.utils.getOrAwaitValueTest

@RunWith(JUnit4::class)
class MainViewModelTest : TestCase() {
    private lateinit var database: AppDatabase
    private lateinit var filmsApi: FilmsApi
    private lateinit var viewModel: MainViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

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
        val value = viewModel.films.getOrAwaitValueTest()
        assert(value.isNotEmpty())
    }

    @Test
    fun errorLoadFilmsTest() = runBlocking {
        val filmsApiStub = FilmsApiStub()
        filmsApiStub.errorSimulate = true
        val viewModel = MainViewModel(filmsApiStub, database)
        viewModel.loadFilms()
        val expectedErrorMessage = ErrorMessage(R.string.network_error)
        assert(viewModel.loadError.getOrAwaitValueTest() == expectedErrorMessage)
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
        assert(viewModel.films.getOrAwaitValueTest().size == 2)
    }
}