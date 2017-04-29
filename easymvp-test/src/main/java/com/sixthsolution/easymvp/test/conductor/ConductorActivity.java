package com.sixthsolution.easymvp.test.conductor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.sixthsolution.easymvp.test.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Saeed Masoumi (s-masoumi@live.com)
 */

public class ConductorActivity extends AppCompatActivity {

    @BindView(R.id.base_layout)
    RelativeLayout view;
    private Router router;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        assertNotNull(view);

        router = Conductor.attachRouter(this, (ViewGroup) findViewById(R.id.container),
                savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(RouterTransaction.with(new TestController()));
        }
    }

}
