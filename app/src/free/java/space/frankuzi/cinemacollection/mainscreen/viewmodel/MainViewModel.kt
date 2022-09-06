package space.frankuzi.cinemacollection.mainscreen.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.data.FilmItem
import space.frankuzi.cinemacollection.data.network.FilmsApi
import space.frankuzi.cinemacollection.data.room.AppDatabase
import space.frankuzi.cinemacollection.mainscreen.model.MainRepository
import space.frankuzi.cinemacollection.utils.LoadIdlingResource
import space.frankuzi.cinemacollection.utils.livedatavariations.SingleLiveEvent
import javax.inject.Inject

class MainViewModel(
    private val api: FilmsApi,
    private val database: AppDatabase
    ) : ViewModel() {

    private var _isLoading = false

    private val _mainRepository = MainRepository(api, database)

    private val _films = MutableLiveData<List<FilmItem>>()
    private val _filmItemChanged = SingleLiveEvent<Int>()
    private val _refreshError = SingleLiveEvent<ErrorMessage>()
    private val _isRefreshing = MutableLiveData<Boolean>()
    private val _isLastPage = MutableLiveData<Boolean>()
    private val _loadError = SingleLiveEvent<ErrorMessage>()

    val films: LiveData<List<FilmItem>> = _films
    val filmItemChanged: LiveData<Int> = _filmItemChanged
    val loadError: LiveData<ErrorMessage> = _loadError
    val refreshError: LiveData<ErrorMessage> = _refreshError
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    val isLastPage: LiveData<Boolean> = _isLastPage

    private var job = Job()
        get() {
            if (field.isCancelled)
                field = Job()
            return field
        }

    fun loadFilms() {
        if (_isLoading)
            return

        if (films.value != null)
            return

        _isLoading = true

        viewModelScope.launch(job) {
            try {
                LoadIdlingResource.increment()
                _films.value = _mainRepository.loadFilms()
                _isLoading = false
                _isLastPage.value = _mainRepository.isLastPage()
                LoadIdlingResource.decrement()
            } catch (httpException: HttpException) {
                _loadError.value = ErrorMessage(R.string.error_code, httpException.code().toString())
            } catch (throwable: Throwable) {
                _loadError.value = ErrorMessage(R.string.network_error)
            }
        }
    }

    fun retryLoadFilm() {
        _isLoading = true;
        viewModelScope.launch {
            try {
                _films.value = _mainRepository.getFilms()
                _isLoading = false
                _isLastPage.value = _mainRepository.isLastPage()
            } catch (httpException: HttpException) {
                _loadError.value = ErrorMessage(R.string.error_code, httpException.code().toString())
            } catch (throwable: Throwable) {
                _loadError.value = ErrorMessage(R.string.network_error)
            }
        }
    }

    fun refreshFilms() {
        if (_isLoading)
            job.cancel()

        _isLoading = true
        _isRefreshing.value = true

        viewModelScope.launch(job) {
            try {
                _films.value = _mainRepository.getFirstPageFilms()
                _isLoading = false
                _isRefreshing.value = false
            } catch (httpException: HttpException) {
                _refreshError.value = ErrorMessage(R.string.error_code, httpException.code().toString())
            } catch (throwable: Throwable) {
                _refreshError.value = ErrorMessage(R.string.network_error)
            }
        }
    }

    fun updateFilm(film: FilmItem) {
        val films = films.value

        val index = films?.indexOfFirst {
            it.id == film.id
        }

        index?.let {
            _filmItemChanged.value = it
        }
    }

    fun loadNextPage() {
        if (_isLoading)
            return

        _isLoading = true

        viewModelScope.launch(job) {
            try {
                _films.value = _mainRepository.getNextPageFilms()
                _isLoading = false
                _isLastPage.value = _mainRepository.isLastPage()
            } catch (httpException: HttpException) {
                _loadError.value = ErrorMessage(R.string.error_code, httpException.code().toString())
            } catch (throwable: Throwable) {
                _loadError.value = ErrorMessage(R.string.network_error)
            }
        }
    }

    fun searchFilmsByName(name: String) {
        job.cancel()

        viewModelScope.launch(job) {
            _isLastPage.value = true
            _films.value = _mainRepository.searchFilmsByName(name)
        }
    }

    fun checkIsLastPages() {
        _isLastPage.value = _mainRepository.isLastPage()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    class MainViewModelFactory @Inject constructor(
        private val api: FilmsApi,
        private val database: AppDatabase
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass == MainViewModel::class.java) {

                MainViewModel(api, database) as T
            } else {
                throw ClassNotFoundException()
            }
        }
    }
}

data class ErrorMessage(
    val errorId: Int,
    val parameters: String? = null
)