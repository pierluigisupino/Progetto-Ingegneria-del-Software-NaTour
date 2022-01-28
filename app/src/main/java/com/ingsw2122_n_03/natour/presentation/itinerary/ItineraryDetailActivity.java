package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import java.util.Objects;

public class ItineraryDetailActivity extends BaseActivity {

    private ConstraintLayout layout;
    private RecyclerView imagesRecyclerView;

    private TextView textViewName;
    private TextView textViewDescription;
    private TextView textViewDuration;
    private TextView textViewDifficulty;

    private Parcelable recyclerViewState;

    private Itinerary itinerary;
    private final ArrayList<byte[]> images = new ArrayList<>();

    private IterController iterController;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityItineraryDetailBinding binding =  ActivityItineraryDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        itinerary = (Itinerary) getIntent().getSerializableExtra("itinerary");

        iterController = IterController.getInstance();
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

        materialToolbar.setNavigationOnClickListener(v -> {
            finish();
            iterController.interruptDownloadSession();
        });

        textViewName.setText(itinerary.getName());
        textViewCreator.setText(getResources().getString(R.string.by_text)+" "+itinerary.getCreator().getName());

        String description = itinerary.getDescription();
        if(description != null)
            textViewDescription.setText(description);
        else
            textViewDescription.setVisibility(View.GONE);

        textViewDuration.setText(itinerary.getDuration().getHourOfDay() + "h & " + itinerary.getDuration().getMinuteOfHour() + "m");
        textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[itinerary.getDifficulty()]);


        GridLayoutManager layoutManager =  new GridLayoutManager(this, 2);
        imagesRecyclerView.setLayoutManager(layoutManager);

        imagesRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int spanCount = 2;
                int spacing = 20;//spazio tra gli items

                if (position >= 0) {
                    int column = position % spanCount;

                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;

                    if (position < spanCount) {
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing;
                } else {
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                }
            }
        });

        imagesRecyclerView.setAdapter(new ImageAdapter(images, false));

        imagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = layoutManager.onSaveInstanceState();
                if(newState == RecyclerView.SCROLL_STATE_IDLE && (layoutManager.findLastCompletelyVisibleItemPosition() == Objects.requireNonNull(imagesRecyclerView.getAdapter()).getItemCount() - 1)) {
                    iterController.retrieveItineraryPhotos();
                }
            }

        });


        startButton.setOnClickListener(v -> iterController.goToActivity(ItineraryDetailActivity.this, FollowItineraryActivity.class, itinerary));

        textViewFeedback.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putSerializable("itinerary", itinerary);
            FeedBackDialog dialog = new FeedBackDialog();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "FeedbackDialog");
        });


        //TODO CURRENT USER
        if(itinerary.getCreator() instanceof Admin){
            editButton.setClickable(true);
            editButton.setVisibility(View.VISIBLE);
        }

        editButton.setOnClickListener(v -> {

            if(cancelEditButton.getVisibility() == View.INVISIBLE){
                cancelEditButton.setClickable(true);
                cancelEditButton.setVisibility(View.VISIBLE);
                editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.success)));
                editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_done));

                Toast.makeText(v.getContext(), "Fa la stessa cosa del feed back?", Toast.LENGTH_SHORT).show();

            }else{
                cancelEditButton.setClickable(false);
                cancelEditButton.setVisibility(View.INVISIBLE);
                editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.primary)));
                editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_edit));

                Toast.makeText(v.getContext(), "Edit salvato", Toast.LENGTH_SHORT).show();
            }
        });

        cancelEditButton.setOnClickListener(v -> {
            cancelEditButton.setClickable(false);
            cancelEditButton.setVisibility(View.INVISIBLE);
            editButton.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.primary)));
            editButton.setImageDrawable(AppCompatResources.getDrawable(ItineraryDetailActivity.this, R.drawable.ic_edit));

            Toast.makeText(v.getContext(), "Edit cancellato", Toast.LENGTH_SHORT).show();
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iterController.interruptDownloadSession();
    }


    @Override
    public void onSuccess(String msg) {
        runOnUiThread(()->
            Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.success))
                    .show()
        );
    }


    @Override
    public void onFail(String msg) {
        runOnUiThread(()->
                Snackbar.make(layout, msg, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(ItineraryDetailActivity.this, R.color.error))
                .show()
        );
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
            itinerary = updatedItinerary;

            textViewName.setText(updatedItinerary.getName());

            String description = updatedItinerary.getDescription();
            if(description != null)
                textViewDescription.setText(description);
            else
                textViewDescription.setVisibility(View.GONE);

            textViewDuration.setText(updatedItinerary.getDuration().getHourOfDay() + "h & " + updatedItinerary.getDuration().getMinuteOfHour() + "m");
            textViewDifficulty.setText(getResources().getStringArray(R.array.difficulties)[updatedItinerary.getDifficulty()]);
        });

    }

}
