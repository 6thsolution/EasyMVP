package com.sixthsolution.easymvp.test;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class FragmentViewTest {
    @Rule
    public ActivityTestRule<SimpleActivityWithFragment> activityRule = new ActivityTestRule<>(
            SimpleActivityWithFragment.class);

    @Test
    public void layout_already_inflated_with_ActivityView_annotation() {
        onView(withId(R.id.base_layout)).check(matches(isDisplayed()));
    }
    @Test
    public void fragment_already_attached(){
        onView(withId(R.id.base_fragment)).check(matches(isDisplayed()));
    }
}
