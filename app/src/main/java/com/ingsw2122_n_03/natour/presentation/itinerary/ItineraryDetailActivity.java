package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityItineraryDetailBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class ItineraryDetailActivity extends BaseActivity {

    private IterController iterController;

    private Itinerary itinerary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityItineraryDetailBinding binding =  ActivityItineraryDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("Itinerary");

        iterController = IterController.getInstance();
        iterController.setItineraryDetailActivity(this);

        MaterialToolbar materialToolbar = binding.topAppBar;

        TextView textViewName = binding.textViewName;
        TextView textViewCreator = binding.textViewCreator;
        TextView textViewDescription = binding.textViewDescription;
        TextView textViewDuration = binding.textViewDuration;
        TextView textViewDifficulty = binding.textViewDifficulty;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        textViewName.setText(itinerary.getName());
        textViewCreator.setText("By " + itinerary.getCreator().getName());
        textViewDescription.setText(itinerary.getDescription());
        textViewDuration.setText(String.valueOf(itinerary.getHoursDuration()) + "h & " + String.valueOf(itinerary.getMinutesDuration()) + "m");
        textViewDifficulty.setText(itinerary.getDifficulty());

    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onFail(String msg) {

    }
}
