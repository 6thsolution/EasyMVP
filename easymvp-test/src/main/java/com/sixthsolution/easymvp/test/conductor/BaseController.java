package com.sixthsolution.easymvp.test.conductor;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class BaseController extends Controller{
    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return null;
    }
}
