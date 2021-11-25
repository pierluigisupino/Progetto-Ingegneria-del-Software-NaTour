package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.R;

public class AddItineraryActivity extends AppCompatActivity {

    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itinerary);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);
        FloatingActionButton editButton = findViewById(R.id.editButton);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        editButton.setOnClickListener(view-> {
            AddItineraryDialog dialog = new AddItineraryDialog();
            dialog.show(getSupportFragmentManager(), "AddItineraryDialog");
        });

    }
}