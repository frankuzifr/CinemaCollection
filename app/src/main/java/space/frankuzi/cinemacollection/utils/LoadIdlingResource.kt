
package space.frankuzi.cinemacollection.utils

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class LoadIdlingResource @Inject constructor() : IdlingResource {
    @Volatile
    private var _callback: IdlingResource.ResourceCallback? = null
    private val _isIdleNow =
        AtomicBoolean(true)

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return _isIdleNow.get()
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        _callback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        _isIdleNow.set(isIdleNow)
        if (isIdleNow && _callback != null) {
            _callback!!.onTransitionToIdle()
        }
    }
}