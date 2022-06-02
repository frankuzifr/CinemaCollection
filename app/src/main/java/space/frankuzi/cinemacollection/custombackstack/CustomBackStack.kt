package space.frankuzi.cinemacollection.custombackstack

class CustomBackStack {
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