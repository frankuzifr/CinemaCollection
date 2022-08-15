package space.frankuzi.cinemacollection.utils.custombackstack

import javax.inject.Inject

class CustomBackStack @Inject constructor(){

    private val backStack: MutableList<FragmentWithBarId> = mutableListOf()

    fun addToBackStack(name: String, navigationBarId: Int) {
        val fragmentWithBarId = FragmentWithBarId(
            name,
            navigationBarId
        )

        val foundedItem = backStack.find {
            it.name == name
        }

        if (foundedItem != null)
            backStack.remove(foundedItem)

        backStack.add(fragmentWithBarId)
    }

    fun popFromBackStack(): FragmentWithBarId? {
        if (backStack.size == 1)
            return null

        backStack.removeLastOrNull()

        return backStack.lastOrNull()
    }

    fun getCount() : Int {
        return backStack.size
    }
}