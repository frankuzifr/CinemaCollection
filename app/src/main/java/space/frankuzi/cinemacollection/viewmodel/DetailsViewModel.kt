package space.frankuzi.cinemacollection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.frankuzi.cinemacollection.data.FilmItem

class DetailsViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<FilmItem>()

    val selectedItem: LiveData<FilmItem> = _selectedItem

    fun setItem(item: FilmItem) {
        item.isSelected = true
        _selectedItem.value = item
    }
}