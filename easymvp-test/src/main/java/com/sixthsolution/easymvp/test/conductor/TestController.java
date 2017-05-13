package com.sixthsolution.easymvp.test.conductor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sixthsolution.easymvp.test.R;
import com.sixthsolution.easymvp.test.TestPresenter;
import com.sixthsolution.easymvp.test.View1;

import easymvp.annotation.Presenter;
import easymvp.annotation.conductor.ConductorController;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

@ConductorController(presenter = TestPresenter.class)
public class TestController extends BaseController implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        assertNotNull(testPresenter);
        assertTrue(testPresenter.isOnViewAttachedCalled());
    }

    @Override
    protected void onDetach(@NonNull View view) {
        super.onDetach(view);
        assertTrue(testPresenter.isOnViewDetachedCalled());
        assertFalse(testPresenter.isViewAttached());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //presenter#onDestroy will be called after this method
        assertNotNull(testPresenter);
    }

}
