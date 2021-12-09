package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class MainActivity extends BaseActivity {

    private AuthController authController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = AuthController.getInstance();
        authController.setMainActivity(MainActivity.this);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        float radius = getResources().getDimension(R.dimen.corner_radious);
        MaterialShapeDrawable materialShapeDrawable = (MaterialShapeDrawable)materialToolbar.getBackground();
        materialShapeDrawable.setShapeAppearanceModel(materialShapeDrawable.getShapeAppearanceModel()
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build());

        materialToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.Add) {
                authController.goToActivity(MainActivity.this, AddItineraryActivity.class);
                return true;
            }

            return false;
        });

    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onFail(String msg) {

    }
}