package space.frankuzi.cinemacollection.ui

import android.view.View
import android.widget.Checkable
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.hamcrest.BaseMatcher
import org.hamcrest.CoreMatchers.isA
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import space.frankuzi.cinemacollection.R
import space.frankuzi.cinemacollection.TestActivity
import space.frankuzi.cinemacollection.utils.LoadIdlingResource


@RunWith(AndroidJUnit4::class)
@SmallTest
class MainScreenTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(TesViewSubscribersHandlertActivity::class.java)

    @Before
    fun registerIdLingResource() {
        activityScenarioRule.scenario.onActivity {

            IdlingRegistry.getInstance().register(LoadIdlingResource.countingIdlingResource)
        }
    }

    @After
    fun unRegisterIdLingResource() {
        IdlingRegistry.getInstance().unregister(LoadIdlingResource.countingIdlingResource)
    }

    @Test
    fun favouriteClickTest() {

        onView(withIndex(withId(R.id.favourite_checkbox), 1))
            .perform(setChecked(true))

        onView(withIndex(withId(R.id.favourite_checkbox), 1))
            .check(matches(isChecked()))
    }
}

fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?> {
    return object : TypeSafeMatcher<View?>() {
        var currentIndex = 0
        override fun describeTo(description: Description) {
            description.appendText("with index: ")
            description.appendValue(index)
            matcher.describeTo(description)
        }

        override fun matchesSafely(view: View?): Boolean {
            return matcher.matches(view) && currentIndex++ == index
        }
    }
}

fun setChecked(checked: Boolean): ViewAction? {
    return object : ViewAction {
        override fun getConstraints(): BaseMatcher<View?> {
            return object : BaseMatcher<View?>() {
                override fun matches(item: Any): Boolean {
                    return isA(Checkable::class.java).matches(item)
                }

                override fun describeMismatch(item: Any, mismatchDescription: Description) = Unit
                override fun describeTo(description: Description) = Unit
            }
        }

        override fun getDescription(): String? {
            return null
        }

        override fun perform(uiController: UiController?, view: View) {
            val checkableView = view as Checkable
            checkableView.isChecked = checked
        }
    }
}