package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.os.Handler;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.StartController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.Objects;

public class SplashActivity extends BaseActivity {

    private StartController startController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startController = StartController.getInstance();

        Objects.requireNonNull(getSupportActionBar()).hide();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startController.configureAmplify(SplashActivity.this);
            }
        }, 800);
    }

    @Override
    public void onSuccess(String msg) {
        //SHOW SNACKBAR
    }

    @Override
    public void onFail(String msg) {
        //SHOW SNACKBAR
    }
}