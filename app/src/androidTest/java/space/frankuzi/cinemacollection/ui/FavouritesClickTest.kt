package space.frankuzi.cinemacollection.ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import junit.framework.Assert.assertFalse
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import space.frankuzi.cinemacollection.MainActivity
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.utils.LoadIdlingResource
import javax.inject.Inject


@RunWith(AndroidJUnit4ClassRunner::class)
@SmallTest
class MainScreenTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

//    @Before
//    fun launchActivity() {
//        val scenario = ActivityScenario.launch(MainActivity::class.java)
//        //scenario.moveToState(Lifecycle.State.DESTROYED);
//    }
//
//    @Inject
//    lateinit var idlingResource: LoadIdlingResource
//    //private var idLingResource: IdlingResource? = null
//
//    @Before
//    fun registerIdLingResource() {
//        activityScenarioRule.scenario.onActivity {
////            idLingResource = it.
//            IdlingRegistry.getInstance().register(idlingResource as IdlingResource)
//        }
//    }
//
//    @After
//    fun unRegisterIdLingResource() {
//        IdlingRegistry.getInstance().unregister(idlingResource)
//    }

    @Test
    fun favouriteClickTest() {
//        val checkboxState = false
//        val activity = getCurrentActivity()
//        val recyclerView = activity?.findViewById<RecyclerView>(R.id.items_container)
//        assertFalse(recyclerView?.adapter?.itemCount == 0)
        onView(withId(R.id.items_container))
            .check(matches(isDisplayed()))
//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

//            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
//                0,
//                ViewActions.click()
//            ))
//
//        onView(withId())
    }
}

//fun atPosition(position: Int, itemMatcher: Matcher<View?>): Matcher<View?>? {
//    checkNotNull(itemMatcher)
//    return object : BoundedMatcher<View?, RecyclerView>(RecyclerView::class.java) {
//        override fun describeTo(description: Description) {
//            description.appendText("has item at position $position: ")
//            itemMatcher.describeTo(description)
//        }
//
//        override fun matchesSafely(view: RecyclerView): Boolean {
//            val viewHolder = view.findViewHolderForAdapterPosition(position)
//                ?: // has no item on such position
//                return false
//            return itemMatcher.matches(viewHolder.itemView)
//        }
//    }
//}
//
//class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
//    override fun check(view: View, noViewFoundException: NoMatchingViewException) {
//        if (noViewFoundException != null) {
//            throw noViewFoundException
//        }
//        val recyclerView = view as RecyclerView
//        val adapter = recyclerView.adapter
//        assertThat(adapter!!.itemCount, `is`(expectedCount))
//    }
//}
//
//fun getCurrentActivity(): Activity? {
//    var currentActivity: Activity? = null
//    getInstrumentation().runOnMainSync { run { currentActivity = ActivityLifecycleMonitorRegistry
//        .getInstance().getActivitiesInStage(Stage.RESUMED).elementAtOrNull(0) } }
//    return currentActivity
//}