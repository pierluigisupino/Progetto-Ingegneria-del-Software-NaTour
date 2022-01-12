package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

        TextView textView1 = binding.textView6;
        TextView textView2 = binding.textView7;
        TextView textView3 = binding.textView8;
        TextView textView4 = binding.textView10;

        textView1.setText(itinerary.getName());
        textView2.setText(itinerary.getCreator().getName());
        textView3.setText(String.valueOf(itinerary.getHoursDuration()));
        textView4.setText(String.valueOf(itinerary.getMinutesDuration()));

    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onFail(String msg) {

    }
}
