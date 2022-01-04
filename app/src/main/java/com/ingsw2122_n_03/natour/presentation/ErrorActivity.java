package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ingsw2122_n_03.natour.databinding.ActivityErrorBinding;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityErrorBinding binding = ActivityErrorBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Button exitButton = binding.exitButton;
        exitButton.setOnClickListener(v -> {
            finish();
            System.exit(1);
        });

    }

}