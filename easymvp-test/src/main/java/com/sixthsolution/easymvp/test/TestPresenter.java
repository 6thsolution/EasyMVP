package com.sixthsolution.easymvp.test;

import android.util.Log;

import easymvp.AbstractPresenter;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

public class TestPresenter extends AbstractPresenter<View1> {

    private boolean isOnViewAttachedCalled = false;
    private boolean isOnViewDetachedCalled = false;

    @Override
    public void onViewAttached(View1 view) {
        super.onViewAttached(view);
        isOnViewAttachedCalled = true;
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
        isOnViewDetachedCalled = true;
        Log.d("TestPresenter","onViewDetached Called");
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        Log.d("TestPresenter","OnDestroyed Called");
    }

    public boolean isOnViewAttachedCalled() {
        return isOnViewAttachedCalled;
    }

    public boolean isOnViewDetachedCalled() {
        return isOnViewDetachedCalled;
    }


}
