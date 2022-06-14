package space.frankuzi.cinemacollection.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import space.frankuzi.cinemacollection.App
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.network.response.GetFilmsResponse

class MainRepository {
    private var _currentPage = 0
    private var _lastPage = 1

    fun getNextPages(getFilmsCallback: GetFilmsCallback) {
        _currentPage++

        getFilms(getFilmsCallback)
    }

    fun retryGetCurrentPage(getFilmsCallback: GetFilmsCallback) {
        getFilms(getFilmsCallback)
    }

    private fun getFilms(getFilmsCallback: GetFilmsCallback) {
        val filmsApi = App.instance.filmsApi

        filmsApi.getFilms(_currentPage)
            .enqueue(object : Callback<GetFilmsResponse> {
                override fun onResponse(
                    call: Call<GetFilmsResponse>,
                    response: Response<GetFilmsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val getFilmsResponse = response.body()
                        getFilmsResponse?.let {
                            _lastPage = it.totalPages
                        }
                        val films = getFilmsResponse?.items?.map {
                            FilmItem(
                                name = it?.nameRu,
                                description = null,
                                imageUrl = it?.posterUrl
                            )
                        }

                        films?.let {
                            getFilmsCallback.onSuccess(it, _currentPage == _lastPage)
                        }
                    } else {
                        getFilmsCallback.onError("Код ошибки: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<GetFilmsResponse>, t: Throwable) {
                    getFilmsCallback.onError("Ошибка подключения...")
                }
            })
    }
}

interface GetFilmsCallback {
    fun onSuccess(films: List<FilmItem>, isLastPage: Boolean)
    fun onError(message: String)
}