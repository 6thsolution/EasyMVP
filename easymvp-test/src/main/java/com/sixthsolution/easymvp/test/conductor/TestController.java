package com.sixthsolution.easymvp.test.conductor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.sixthsolution.easymvp.test.R;
import com.sixthsolution.easymvp.test.TestPresenter;
import com.sixthsolution.easymvp.test.View1;

import easymvp.annotation.conductor.ConductorController;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

@ConductorController(presenter = TestPresenter.class)
public class TestController extends Controller implements View1 {

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.simple_fragment, container, false);
    }
}
