package com.sixthsolution.easymvp.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import easymvp.annotation.CustomView;
import easymvp.annotation.Presenter;
import easymvp.annotation.PresenterId;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@CustomView(presenter = TestPresenter.class)
public class SimpleCustomView extends View implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @PresenterId
    int presenterId = 1_000;


    public SimpleCustomView(Context context) {
        super(context);
    }

    public SimpleCustomView(Context context,
                            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SimpleCustomView(AppCompatActivityWithMultiViews context, int counter) {
        super(context);
        presenterId += counter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        assertNotNull(testPresenter);
        assertTrue(testPresenter.isOnViewAttachedCalled());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
