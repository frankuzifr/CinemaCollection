
package space.frankuzi.cinemacollection.utils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

object LoadIdlingResource  {

    private const val RESOURCE = "GLOBAL"

    @JvmField val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow)
            countingIdlingResource.decrement()
    }
}