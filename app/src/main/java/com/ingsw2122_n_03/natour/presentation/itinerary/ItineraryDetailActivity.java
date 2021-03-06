package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.application.MessageController;
import com.ingsw2122_n_03.natour.databinding.ActivityItineraryDetailBinding;
import com.ingsw2122_n_03.natour.infastructure.implementations.AmplifyImplementations.Analytics;
import com.ingsw2122_n_03.natour.model.Admin;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.dialogs.AdminDialog;
import com.ingsw2122_n_03.natour.presentation.dialogs.DeleteItineraryDialog;
import com.ingsw2122_n_03.natour.presentation.dialogs.FeedBackDialog;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;
import com.ingsw2122_n_03.natour.presentation.support.GridSpacingItemDecoration;
import com.ingsw2122_n_03.natour.presentation.support.ImageAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ItineraryDetailActivity extends BaseActivity {

    private ConstraintLayout layout;
    private RecyclerView imagesRecyclerView;

    private TextView textViewName;
    private TextView textViewDescription;
    private TextView textViewDuration;
    private TextView textViewDifficulty;
    private TextView textViewWarning;

    private Parcelable recyclerViewState;
    private Bundle bundle;

    private final ArrayList<byte[]> images = new ArrayList<>();

    private IterController iterController;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityItineraryDetailBinding binding =  ActivityItineraryDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Itinerary itinerary = (Itinerary) getIntent().getSerializableExtra("itinerary");
        User currentUser = (User) getIntent().getSerializableExtra("user");

        bundle = new Bundle();
        bundle.putSerializable("itinerary", itinerary);

        iterController = IterController.getInstance();
        iterController.setItineraryDetailActivity(this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        textViewName = binding.textViewName;
        TextView textViewCreator = binding.textViewCreator;
        textViewDescription = binding.textViewDescription;
        textViewDuration = binding.textViewDuration;
        textViewDifficulty = binding.textViewDifficulty;
        textViewWarning = binding.warning;

        TextView textViewFeedback = binding.textViewFeedBack2;

        Button startButton = binding.startButton;
        imagesRecyclerView = binding.imagesRecyclerView;
        FloatingActionButton editButton = binding.editButton;
        FloatingActionButton deleteButton = binding.deleteButton;

        materialToolbar.setNavigationOnClickListener(v -> {
            finish();
            iterController.interruptDownloadSession();
        });

        textViewName.setText(itinerary.getName());
        textViewCreator.setText(getResources().getString(R.string.by_text)+" "+itinerary.getCreator().getName());

        if(!itinerary.getCreator().getName().equals("Unknown") && !itinerary.getCreator().getUid().equals(currentUser.getUid())) {
            textViewCreator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chat, 0);
            textViewCreator.setClickable(true);
            textViewCreator.setOnClickListener(v -> MessageController.getInstance().retrieveMessages(itinerary.getCreator()));
        }

        String description = itinerary.getDescription();
        if(description != null)
            textViewDescription.setText(description);
        else
            textViewDescription.setVisibility(View.GONE);

        if(itinerary.getEditDate() != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yy");
            String shareDate = dateFormat.format(itinerary.getEditDate());
            textViewWarning.setVisibility(View.VISIBLE);
            textViewWarning.setText(getString(R.string.edited_admin)+" "+shareDate);
        }

        textViewDuration.setText(itinerary.getDuration().getHourOfDay() + "h & " + itinerary.getDuration().getMinuteOfHour() + "m");
        textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[itinerary.getDifficulty()]);


        GridLayoutManager layoutManager =  new GridLayoutManager(this, 2);
        imagesRecyclerView.setLayoutManager(layoutManager);
        imagesRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 30));

        imagesRecyclerView.setAdapter(new ImageAdapter(images, false));

        imagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = layoutManager.onSaveInstanceState();
                if(newState == RecyclerView.SCROLL_STATE_IDLE && (layoutManager.findLastCompletelyVisibleItemPosition() == Objects.requireNonNull(imagesRecyclerView.getAdapter()).getItemCount() - 1))
                    iterController.retrieveItineraryPhotos();
            }

        });


        startButton.setOnClickListener(v -> {
            Analytics.recordPositiveEvent("FollowItinerary");
            iterController.goToActivity(ItineraryDetailActivity.this, FollowItineraryActivity.class, itinerary);
        });

        textViewFeedback.setOnClickListener(v -> {
            FeedBackDialog dialog = new FeedBackDialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "FeedbackDialog");
        });


        if(currentUser instanceof Admin){
            editButton.setClickable(true);
            editButton.setVisibility(View.VISIBLE);

            deleteButton.setClickable(true);
            deleteButton.setVisibility(View.VISIBLE);
        }


        deleteButton.setOnClickListener(v -> {
            DeleteItineraryDialog dialog = new DeleteItineraryDialog();
            dialog.show(getSupportFragmentManager(), "DeleteDialog");
        });

        editButton.setOnClickListener(v -> {
            AdminDialog dialog = new AdminDialog();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "AdminDialog");
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iterController.interruptDownloadSession();
    }


    @Override
    public void onSuccess(String msg) {
        runOnUiThread(()->{
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.success));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }


    @Override
    public void onFail(String msg) {
        runOnUiThread(() -> {
            Snackbar snackbar = Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.error));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);
            snackbar.show();
        });
    }


    private void setAdapter(){
        runOnUiThread(()->{
            imagesRecyclerView.setAdapter(new ImageAdapter(images, false));
            Objects.requireNonNull(imagesRecyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);
        });
    }


    public void updateImages(byte[] image) {
        this.images.add(image);
        setAdapter();
    }


    @SuppressLint("SetTextI18n")
    public void updateItineraryViews(Itinerary updatedItinerary) {
        runOnUiThread(()->{

            bundle.putSerializable("itinerary", updatedItinerary);
            textViewName.setText(updatedItinerary.getName());

            String description = updatedItinerary.getDescription();
            if(description != null)
                textViewDescription.setText(description);
            else
                textViewDescription.setVisibility(View.GONE);

            if(updatedItinerary.getEditDate() != null){
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat= new SimpleDateFormat("dd/MM/yy");
                String shareDate = dateFormat.format(updatedItinerary.getEditDate());
                textViewWarning.setVisibility(View.VISIBLE);
                textViewWarning.setText(getString(R.string.edited_admin)+" "+shareDate);
            }

            textViewDuration.setText(updatedItinerary.getDuration().getHourOfDay() + "h & " + updatedItinerary.getDuration().getMinuteOfHour() + "m");
            textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[updatedItinerary.getDifficulty()]);

        });
    }

}
