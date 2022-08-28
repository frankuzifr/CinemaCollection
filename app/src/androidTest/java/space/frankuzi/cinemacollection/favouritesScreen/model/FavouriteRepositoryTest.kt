package space.frankuzi.cinemacollection.favouritesScreen.model

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.data.room.entity.FavouriteFilmDbEntity

@RunWith(JUnit4::class)
class FavouriteRepositoryTest : TestCase() {
    private lateinit var database: AppDatabase
    private lateinit var favouriteRepository: FavouriteRepository

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        favouriteRepository = FavouriteRepository(database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getFavouritesFilmsWhenDatabaseEmpty() = runBlocking {
        val films = favouriteRepository.getFavouritesFilms()
        assert(films.isEmpty())
    }

    @Test
    fun getFavouritesFilmsWhenDatabaseNotEmpty() = runBlocking {
        database.getFavouritesDao().addToFavourite(FavouriteFilmDbEntity(
            id = 0,
            kinopoiskId = 444,
            nameOriginal = "Tat",
            nameRussian = "Тат",
            description = "Some",
            imageUrl = "https://",
            type = null
        ))

        val films = favouriteRepository.getFavouritesFilms()
        assert(films.isNotEmpty())
    }
}