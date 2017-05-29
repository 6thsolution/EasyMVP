package com.sixthsolution.easymvp.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.sixthsolution.easymvp.test.di.ActivityComponent;
import com.sixthsolution.easymvp.test.di.DaggerActivityComponent;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import easymvp.annotation.ActivityView;
import easymvp.annotation.Presenter;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@ActivityView(presenter = TestPresenter.class, layout = R.layout.activity_multi_views)
public class AppCompatActivityWithMultiViewsWithDagger extends AppCompatActivity implements View1 {

    @Presenter
    TestPresenter testPresenter;

    @BindView(R.id.container)
    LinearLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityComponent component =
                DaggerActivityComponent.builder().build();
        component.injectTo(this);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        assertNotNull(container);
        for (int i = 0; i < 10; i++) {
            View view = new SimpleCustomViewWithDagger(this, i, component);
            container.addView(view);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Set<Integer> ids = new HashSet<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            SimpleCustomViewWithDagger childView =
                    (SimpleCustomViewWithDagger) container.getChildAt(i);
            ids.add(childView.testPresenter.count);
        }
        assertThat(ids.size(), is(10));
        Set<TestPresenter> presenters = new HashSet<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            SimpleCustomViewWithDagger childView =
                    (SimpleCustomViewWithDagger) container.getChildAt(i);
            presenters.add(childView.testPresenter);
        }
        assertThat(presenters.size(), is(10));
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
