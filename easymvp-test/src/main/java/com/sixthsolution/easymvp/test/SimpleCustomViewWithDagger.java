package com.sixthsolution.easymvp.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sixthsolution.easymvp.test.di.ActivityComponent;
import com.sixthsolution.easymvp.test.di.CustomViewComponent;

import javax.inject.Inject;

import easymvp.annotation.CustomView;
import easymvp.annotation.Presenter;
import easymvp.annotation.PresenterId;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CustomView(presenter = TestPresenter.class)
public class SimpleCustomViewWithDagger extends View implements View1 {

    @Inject
    @Presenter
    TestPresenter testPresenter;

    @PresenterId
    int presenterId = 1_000;


    public SimpleCustomViewWithDagger(Context context) {
        super(context);
    }

    public SimpleCustomViewWithDagger(Context context,
                                      @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleCustomViewWithDagger(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SimpleCustomViewWithDagger(Context context, int counter, ActivityComponent component) {
        super(context);
        presenterId += counter;
        component.customViewComponent().build().injectTo(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        assertNotNull(testPresenter);
        assertTrue(testPresenter.isOnViewAttachedCalled());
    }
}
