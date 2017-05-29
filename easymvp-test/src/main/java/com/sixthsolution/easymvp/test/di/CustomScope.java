package com.sixthsolution.easymvp.test.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomScope {
}
