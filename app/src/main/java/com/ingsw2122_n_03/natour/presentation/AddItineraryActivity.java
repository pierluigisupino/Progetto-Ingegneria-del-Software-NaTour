package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;

public class AddItineraryActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private MaterialToolbar materialToolbar;
    private StepView stepView;

    private BlankFragment blankFragment = new BlankFragment();
    private BlankFragment2 blankFragment2 = new BlankFragment2();
    private BlankFragment3 blankFragment3 = new BlankFragment3();

    private Button backButton;
    private Button nextButton;
    private Button addButton;

    private int stepIndex = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_itinerary);

        layout = findViewById(R.id.layout);
        materialToolbar = findViewById(R.id.topAppBar);
        stepView = findViewById(R.id.stepView);
        backButton = findViewById(R.id.back_button);
        nextButton = findViewById(R.id.next_button);
        addButton = findViewById(R.id.add_button);

        materialToolbar.setNavigationOnClickListener(view -> finish());

        stepView.getState()
                .animationType(StepView.ANIMATION_CIRCLE)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        backButton.setVisibility(View.INVISIBLE);
        backButton.setEnabled(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stepIndex > 0){
                    stepIndex--;
                    changeFragment();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stepIndex < 2){
                    stepIndex++;
                    changeFragment();
                }
            }
        });

        addButton.setVisibility(View.INVISIBLE);
        addButton.setEnabled(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddItineraryActivity.this, "Added", Toast.LENGTH_SHORT).show();
            }
        });

        addFragment();
    }

    private void addFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragmentContainer, blankFragment);

        fragmentTransaction.commit();
    }

    private void changeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(stepIndex == 0){
            fragmentTransaction.replace(R.id.fragmentContainer, blankFragment);
        }else if(stepIndex == 1){
            fragmentTransaction.replace(R.id.fragmentContainer, blankFragment2);
        }else if(stepIndex == 2){
            fragmentTransaction.replace(R.id.fragmentContainer, blankFragment3);
        }

        fragmentTransaction.commit();
        stepView.go(stepIndex, true);
        showBackButton();
        showAddButton();
    }

    private void showBackButton(){
        if (stepIndex > 0) {
            backButton.setVisibility(View.VISIBLE);
            backButton.setEnabled(true);
        } else {
            backButton.setVisibility(View.INVISIBLE);
            backButton.setEnabled(false);
        }
    }

    private void showAddButton(){
        if(stepIndex == 2){
            nextButton.setVisibility(View.INVISIBLE);
            nextButton.setEnabled(false);

            addButton.setVisibility(View.VISIBLE);
            addButton.setEnabled(true);
        }else{
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setEnabled(true);

            addButton.setVisibility(View.INVISIBLE);
            addButton.setEnabled(false);
        }
    }
}