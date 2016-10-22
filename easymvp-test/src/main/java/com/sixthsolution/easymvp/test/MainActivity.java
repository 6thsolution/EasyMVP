package com.sixthsolution.easymvp.test;

import android.support.v7.app.AppCompatActivity;

import easymvp.annotation.ActivityView;

@ActivityView(presenter = Presenter1.class, layout = R.layout.activity_main)
public class MainActivity extends AppCompatActivity implements View1 {

}
