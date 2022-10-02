package space.frankuzi.cinemacollection.stubs

import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.network.response.FilmItemResponse
import space.frankuzi.cinemacollection.data.network.response.FilmWithDescriptionResponse
import space.frankuzi.cinemacollection.data.network.response.GetFilmsResponse
import space.frankuzi.cinemacollection.data.room.entity.FilmDbEntity

class FilmsApiStub : FilmsApi {
    var errorSimulate = false

    override suspend fun getFilms(
        pageNumber: Int,
        minRating: Int?,
        maxRating: Int?,
        minYear: Int?,
        maxYear: Int?
    ): GetFilmsResponse {
        if (!errorSimulate) {
            return GetFilmsResponse(
                total = 20,
                totalPages = 20,
                items = listOf(
                    FilmItemResponse(
                        kinopoiskId = 444,
                        nameRu = "Ru",
                        nameEn = "En",
                        nameOriginal = "Original",
                        posterUrl = "https://"
                    )
                )
            )
        } else {
            throw Throwable()
        }
    }

    override suspend fun getFilmById(filmId: Int): FilmWithDescriptionResponse {
        return FilmWithDescriptionResponse(
            nameRu = "Ru",
            nameEn = "En",
            nameOriginal = "Original",
            posterUrl = "https://",
            description = "Some"
        )
    }
}