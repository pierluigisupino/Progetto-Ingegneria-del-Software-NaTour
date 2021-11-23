package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;

public class AddItineraryActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itinerary);

        MaterialToolbar materialToolbar = findViewById(R.id.topAppBar);

        autoCompleteTextView = findViewById(R.id.difficultyAutoComplete);

        String[] difficulties = new String[]{
                "diff 1",
                "diff 2",
                "diff 3",
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.difficult_list_item, difficulties);

        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString(), false);

    }
}