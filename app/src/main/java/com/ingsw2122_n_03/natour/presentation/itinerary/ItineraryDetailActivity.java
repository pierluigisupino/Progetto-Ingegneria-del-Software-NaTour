package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityItineraryDetailBinding;
import com.ingsw2122_n_03.natour.model.Admin;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.FeedBackDialog;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;
import com.ingsw2122_n_03.natour.presentation.support.ImageAdapter;

import java.util.ArrayList;

public class ItineraryDetailActivity extends BaseActivity {

    private ConstraintLayout layout;
    private RecyclerView imagesRecyclerView;

    private TextView textViewName;
    private TextView textViewDescription;
    private TextView textViewDuration;
    private TextView textViewDifficulty;

    private Itinerary itinerary;
    private final ArrayList<byte[]> images = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityItineraryDetailBinding binding =  ActivityItineraryDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        itinerary = (Itinerary) intent.getSerializableExtra("itinerary");

        IterController controller = IterController.getInstance();

        IterController iterController = IterController.getInstance();
        iterController.setItineraryDetailActivity(this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        textViewName = binding.textViewName;
        TextView textViewCreator = binding.textViewCreator;
        textViewDescription = binding.textViewDescription;
        textViewDuration = binding.textViewDuration;
        textViewDifficulty = binding.textViewDifficulty;

        TextView textViewFeedback = binding.textViewFeedBack2;

        Button startButton = binding.startButton;
        imagesRecyclerView = binding.imagesRecyclerView;
        FloatingActionButton cancelEditButton = binding.cancelEditButton;
        FloatingActionButton editButton = binding.editButton;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        textViewName.setText(itinerary.getName());
        textViewCreator.setText(getResources().getString(R.string.by_text)+" "+itinerary.getCreator().getName());

        String description = itinerary.getDescription();
        if(description != null)
            textViewDescription.setText(description);
        else
            textViewDescription.setVisibility(View.GONE);

        textViewDuration.setText(itinerary.getDuration().getHourOfDay() + "h & " + itinerary.getDuration().getMinuteOfHour() + "m");
        textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[itinerary.getDifficulty()]);

        startButton.setOnClickListener(v -> {
            controller.goToActivity(ItineraryDetailActivity.this, FollowItineraryActivity.class, itinerary);
        });


        // TODO: 19/01/2022 da dove prendiamo la lista di bitmap ?
        //TODO: CI STA GIA, MA PERCHE SETTARE QUI L'ADAPTER? (VEDI SOTTO)
        //imagesRecyclerView.setAdapter(new ImageAdapter(images));

        textViewFeedback.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("itinerary", itinerary);
            FeedBackDialog dialog = new FeedBackDialog();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "FeedbackDialog");
        });

        if(itinerary.getCreator() instanceof Admin){
            editButton.setClickable(true);
            editButton.setVisibility(View.VISIBLE);
        }

        editButton.setOnClickListener(view12 -> {

            if(cancelEditButton.getVisibility() == View.INVISIBLE){
                cancelEditButton.setClickable(true);
                cancelEditButton.setVisibility(View.VISIBLE);
                editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.success)));
                editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_done));

                Toast.makeText(view12.getContext(), "Fa la stessa cosa del feed back?", Toast.LENGTH_SHORT).show();

            }else{
                cancelEditButton.setClickable(false);
                cancelEditButton.setVisibility(View.INVISIBLE);
                editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.primary)));
                editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_edit));

                Toast.makeText(view12.getContext(), "Edit salvato", Toast.LENGTH_SHORT).show();
            }
        });

        cancelEditButton.setOnClickListener(view1 -> {
            cancelEditButton.setClickable(false);
            cancelEditButton.setVisibility(View.INVISIBLE);
            editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.primary)));
            editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_edit));

            Toast.makeText(view1.getContext(), "Edit cancellato", Toast.LENGTH_SHORT).show();
        });

    }


    @Override
    public void onSuccess(String msg) {

        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.success))
                .show();

    }


    @Override
    public void onFail(String msg) {

        Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.error))
                .show();

    }


    private void setAdapter(){

    }


    public void updateImages(ArrayList<byte[]> images) {
        this.images.addAll(images);
        setAdapter();
    }


    public void updateItineraryView(Itinerary updatedItinerary) {

        itinerary = updatedItinerary;

        textViewName.setText(itinerary.getName());

        String description = itinerary.getDescription();
        if(description != null)
            textViewDescription.setText(description);
        else
            textViewDescription.setVisibility(View.GONE);

        textViewDuration.setText(itinerary.getDuration().getHourOfDay() + "h & " + itinerary.getDuration().getMinuteOfHour() + "m");
        textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[itinerary.getDifficulty()]);

    }

}
