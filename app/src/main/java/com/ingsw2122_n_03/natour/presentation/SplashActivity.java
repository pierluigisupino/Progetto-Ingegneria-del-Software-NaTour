package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.databinding.ActivitySplashBinding;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class SplashActivity extends BaseActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        authController = AuthController.getInstance();
        authController.setSplashActivity(SplashActivity.this);

        final Handler handler = new Handler();
        handler.postDelayed(() -> authController.setUp(), 800);
    }

    @Override
    public void onSuccess(String msg) { }

    @Override
    public void onFail(String msg) { }
}