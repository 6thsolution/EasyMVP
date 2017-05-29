package com.sixthsolution.easymvp.test.di;

import com.sixthsolution.easymvp.test.SimpleCustomViewWithDagger;

import dagger.Subcomponent;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@CustomScope
@Subcomponent(modules = CustomModule.class)
public interface CustomViewComponent {

    void injectTo(SimpleCustomViewWithDagger simpleCustomViewWithDagger);

    @Subcomponent.Builder
    interface Builder {
        CustomViewComponent build();
    }
}
