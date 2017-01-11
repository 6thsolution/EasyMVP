package com.sixthsolution.easymvp.test;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@FragmentView(presenter = TestPresenter.class)
public class SimpleFragment extends Fragment implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        assertNotNull(testPresenter);
        assertTrue(testPresenter.isOnViewAttachedCalled());
    }

    @Override
    public void onPause() {
        super.onPause();
        assertTrue(testPresenter.isOnViewDetachedCalled());
        assertFalse(testPresenter.isViewAttached());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        assertNull(testPresenter);
    }

}
