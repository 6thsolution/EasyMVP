package com.sixthsolution.easymvp.test.di;

import com.sixthsolution.easymvp.test.TestPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Module
public class CustomModule {

    @CustomScope
    @Provides
    public TestPresenter providePresenter() {
        return new TestPresenter();
    }
}
