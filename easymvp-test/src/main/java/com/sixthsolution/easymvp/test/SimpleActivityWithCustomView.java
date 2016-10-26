package com.sixthsolution.easymvp.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import easymvp.annotation.ActivityView;
import easymvp.annotation.Presenter;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

@ActivityView(presenter = TestPresenter.class)
public class SimpleActivityWithCustomView extends AppCompatActivity implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new SimpleCustomView(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        assertNotNull(testPresenter);
        assertTrue(testPresenter.isOnViewAttachedCalled());
    }

    @Override
    protected void onStop() {
        super.onStop();
        assertTrue(testPresenter.isOnViewDetachedCalled());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        assertNull(testPresenter);
    }
}
