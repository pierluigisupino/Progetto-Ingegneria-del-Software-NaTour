package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class MainActivity extends BaseActivity {

    private AuthController authController;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authController = AuthController.getInstance();
        authController.setMainActivity(MainActivity.this);

        layout = (ConstraintLayout) findViewById(R.id.layout);

        Button signOutButton = (Button) findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(view -> authController.signOut(MainActivity.this));

    }

    public void onSuccess(String msg) {

        runOnUiThread(() -> Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(MainActivity.this, R.color.success))
                .show());
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(MainActivity.this, R.color.error))
                .show());
    }
}