package space.frankuzi.cinemacollection.mainScreen.model

import android.app.Application
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase

class MainRepositoryTest {
    @Mock
    lateinit var filmsApi: FilmsApi

    @Mock
    lateinit var database: AppDatabase

    lateinit var mainRepository: MainRepository

    @Before
    fun setUp() {
        mainRepository = MainRepository(filmsApi, database, App as Application)
    }

    fun getFilmsFromDatabaseTest() {
        //val expected =
    }
}