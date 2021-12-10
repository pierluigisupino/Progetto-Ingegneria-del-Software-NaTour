package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;
import com.shuhart.stepview.StepView;

public class AddItineraryActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private MaterialToolbar materialToolbar;
    private StepView stepView;

    private AddItineraryFragment1 addItineraryFragment1 = new AddItineraryFragment1();
    private AddItineraryFragment2 addItineraryFragment2 = new AddItineraryFragment2();
    private AddItineraryFragment3 addItineraryFragment3;

    private Button backButton;
    private Button nextButton;

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

        materialToolbar.setNavigationOnClickListener(view -> finish());

        stepView.getState()
                .animationType(StepView.ANIMATION_CIRCLE)
                .animationDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .commit();

        backButton.setVisibility(View.INVISIBLE);
        backButton.setEnabled(false);

        backButton.setOnClickListener(v -> {
            if(stepIndex > 0){
                stepIndex--;
                changeFragment();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (stepIndex == 0 && addItineraryFragment1.isNameValid()){
                stepIndex++;
                changeFragment();
            }else if(stepIndex == 1 && (addItineraryFragment2.getHours() != 0 || addItineraryFragment2.getMinutes() != 0)){
                stepIndex++;
                changeFragment();
            }else if(stepIndex == 2){
                Toast.makeText(this, "Add pressed", Toast.LENGTH_SHORT).show();
            }
        });

        addFragment();
    }

    private void addFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, addItineraryFragment1);
        fragmentTransaction.commit();
    }

    private void changeFragment(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if(stepIndex == 0){
            fragmentTransaction.replace(R.id.fragmentContainer, addItineraryFragment1);
        }else if(stepIndex == 1){
            fragmentTransaction.replace(R.id.fragmentContainer, addItineraryFragment2);
        }else if(stepIndex == 2){

            String name = addItineraryFragment1.getName();
            String description = addItineraryFragment1.getDescription();
            String difficulty = addItineraryFragment2.getDifficulty();
            int hours = addItineraryFragment2.getHours();
            int minutes = addItineraryFragment2.getMinutes();

            addItineraryFragment3 = AddItineraryFragment3.newInstance(name, description, difficulty, hours, minutes);
            fragmentTransaction.replace(R.id.fragmentContainer, addItineraryFragment3);
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
            nextButton.setText("Add");
        }else{
            nextButton.setText("Next"); //(R.string...)
        }
    }
}