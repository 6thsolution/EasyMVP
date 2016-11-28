package com.sixthsolution.easymvp.test;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

public class CustomViewTest {

    @Rule
    public ActivityTestRule<SimpleAppCompatActivityWithCustomView> activityRule = new ActivityTestRule<>(
            SimpleAppCompatActivityWithCustomView.class);

    @Test
    public void custom_view_already_attached() {
        View contentView = activityRule.getActivity().findViewById(android.R.id.content);
    }

}
