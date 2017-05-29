package com.sixthsolution.easymvp.test.di;

import com.sixthsolution.easymvp.test.AppCompatActivityWithMultiViewsWithDagger;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Singleton
@Component
public interface ActivityComponent {

    CustomViewComponent.Builder customViewComponent();

    void injectTo(AppCompatActivityWithMultiViewsWithDagger activity);
}
