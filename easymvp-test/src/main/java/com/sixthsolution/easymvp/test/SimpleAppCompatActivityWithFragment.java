package com.sixthsolution.easymvp.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import easymvp.annotation.ActivityView;
import easymvp.annotation.Presenter;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@ActivityView(presenter = TestPresenter.class, layout = R.layout.activity_main)
public class SimpleAppCompatActivityWithFragment extends AppCompatActivity implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.container,new SimpleFragment()).commit();
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
        assertFalse(testPresenter.isViewAttached());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        assertNull(testPresenter);
    }


}
