package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityAddItineraryBinding;
import com.ingsw2122_n_03.natour.presentation.dialogs.LostProgressDialog;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;
import com.shuhart.stepview.StepView;

import org.joda.time.LocalTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class AddItineraryActivity extends BaseActivity {

    private StepView stepView;

    private final AddItineraryFragment1 addItineraryFragment1 = new AddItineraryFragment1();
    private final AddItineraryFragment2 addItineraryFragment2 = new AddItineraryFragment2(this);
    private final AddItineraryFragment3 addItineraryFragment3 = new AddItineraryFragment3(this);
    private final AddItineraryFragment4 addItineraryFragment4 = new AddItineraryFragment4(this);

    private ConstraintLayout layout;
    private Button backButton;
    private Button nextButton;
    private LinearProgressIndicator linearProgressIndicator;

    private int stepIndex = 0;

    private String name;
    private String description;
    private int difficulty;
    private LocalTime duration;
    private ArrayList <byte[]> imagesBytes = new ArrayList<>();
    private ArrayList<GeoPoint> waypoints = new ArrayList<>();

    private IterController iterController;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityAddItineraryBinding binding = ActivityAddItineraryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        iterController = IterController.getInstance();
        iterController.setAddItineraryActivity(this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;
        stepView = binding.stepView;
        backButton = binding.backButton;
        nextButton = binding.nextButton;
        linearProgressIndicator = binding.progressBar;

        materialToolbar.setNavigationOnClickListener(v ->{
            LostProgressDialog dialog = new LostProgressDialog();
            dialog.show(getSupportFragmentManager(), "LostProgressDialog");
        });

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
                name = addItineraryFragment1.getName();
                description = addItineraryFragment1.getDescription();
                stepIndex++;
                changeFragment();
            }else if(stepIndex == 1 && addItineraryFragment2.isDurationValid()){
                difficulty = addItineraryFragment2.getDifficulty();
                duration = addItineraryFragment2.getDuration();
                stepIndex++;
                changeFragment();
            }else if(stepIndex == 2){
                imagesBytes = addItineraryFragment3.getImagesBytes();
                addItineraryFragment4.setRawPointOfInterests(iterController.calculatePhotoPosition(imagesBytes));
                addItineraryFragment4.setImageBytes(imagesBytes);
                stepIndex++;
                changeFragment();
            } else if(stepIndex == 3 && addItineraryFragment4.isStartPointInserted() && addItineraryFragment4.arePositionsCorrect()){
                waypoints = addItineraryFragment4.getWaypoints();
                iterController.insertItinerary(name, description, difficulty, duration, imagesBytes, waypoints);
            }

        });

        addFragment();

    }

    @Override
    public void onBackPressed() {
        LostProgressDialog dialog = new LostProgressDialog();
        dialog.show(getSupportFragmentManager(), "LostProgressDialog");
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
            fragmentTransaction.replace(R.id.fragmentContainer, addItineraryFragment3);
        }else if(stepIndex == 3){
            fragmentTransaction.replace(R.id.fragmentContainer, addItineraryFragment4);
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

        if(stepIndex == 3)
            nextButton.setText(R.string.add_button);
        else
            nextButton.setText(R.string.next_button);

    }

    public void showProgressBar(){

        backButton.setEnabled(false);
        nextButton.setEnabled(false);
        linearProgressIndicator.setVisibility(View.VISIBLE);

    }

    public void hideProgressBar(){

        linearProgressIndicator.setVisibility(View.INVISIBLE);
        backButton.setEnabled(true);
        nextButton.setEnabled(true);

    }

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            linearProgressIndicator.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(AddItineraryActivity.this, R.color.success))
                    .show();
        });

    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            linearProgressIndicator.setVisibility(View.INVISIBLE);
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(AddItineraryActivity.this, R.color.error))
                    .show();
        });

    }

}