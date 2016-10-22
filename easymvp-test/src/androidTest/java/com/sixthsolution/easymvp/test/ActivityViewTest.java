package com.sixthsolution.easymvp.test;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityViewTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);
}
