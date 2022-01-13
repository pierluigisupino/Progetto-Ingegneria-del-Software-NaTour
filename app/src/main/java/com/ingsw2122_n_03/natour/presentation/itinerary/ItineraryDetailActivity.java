package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityItineraryDetailBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class ItineraryDetailActivity extends BaseActivity {

    private ConstraintLayout layout;

    private IterController iterController;
    private Itinerary itinerary;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityItineraryDetailBinding binding =  ActivityItineraryDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("itinerary");

        iterController = IterController.getInstance();
        iterController.setItineraryDetailActivity(this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        TextView textViewName = binding.textViewName;
        TextView textViewCreator = binding.textViewCreator;
        ImageView imageView = binding.image;
        TextView textViewDescription = binding.textViewDescription;
        TextView textViewDuration = binding.textViewDuration;
        TextView textViewDifficulty = binding.textViewDifficulty;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        textViewName.setText(itinerary.getName());
        textViewCreator.setText(getResources().getString(R.string.by_text)+" "+itinerary.getCreator().getName());

        String description = itinerary.getDescription();

        if(!description.equals("null")) {
            textViewDescription.setText(description);
        }else{
            textViewDescription.setVisibility(View.GONE);
        }

        textViewDuration.setText(itinerary.getHoursDuration() + "h & " + itinerary.getMinutesDuration() + "m");
        textViewDifficulty.setText(itinerary.getDifficulty());

    }

    @Override
    public void onSuccess(String msg) {

        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.success))
                .show();

    }

    @Override
    public void onFail(String msg) {

        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.error))
                .show();

    }

}
