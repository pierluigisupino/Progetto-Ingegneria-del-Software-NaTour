package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityItineraryDetailBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.presentation.FeedBackDialog;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.ArrayList;

public class ItineraryDetailActivity extends BaseActivity {

    private ConstraintLayout layout;

    private IterController iterController;
    private Itinerary itinerary;
    private final ArrayList<byte[]> images = new ArrayList<>();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkSettingsAndStartLocationUpdates();
                } else {
                    showAlertGpsPermissionNeeded();
                }
            });

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

        iterController = IterController.getInstance();
        iterController.setItineraryDetailActivity(this);

        layout = binding.layout;
        MaterialToolbar materialToolbar = binding.topAppBar;

        TextView textViewName = binding.textViewName;
        TextView textViewCreator = binding.textViewCreator;
        TextView textViewDescription = binding.textViewDescription;
        TextView textViewDuration = binding.textViewDuration;
        TextView textViewDifficulty = binding.textViewDifficulty;

        TextView textViewFeedback = binding.textViewFeedBack2;

        Button startButton = binding.startButton;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        textViewName.setText(itinerary.getName());
        textViewCreator.setText(getResources().getString(R.string.by_text)+" "+itinerary.getCreator().getName());

        String description = itinerary.getDescription();
        if(description != null)
            textViewDescription.setText(description);
        else
            textViewDescription.setVisibility(View.GONE);

        textViewDuration.setText(itinerary.getHoursDuration() + "h & " + itinerary.getMinutesDuration() + "m");
        textViewDifficulty.setText(itinerary.getDifficulty());

        startButton.setOnClickListener(view1 -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                controller.goToActivity(ItineraryDetailActivity.this, FollowItineraryActivity.class, itinerary);
            } else {
                askLocationPermission();
            }
        });

        textViewFeedback.setOnClickListener(view12 -> {
            Bundle args = new Bundle();
            args.putSerializable("itinerary", itinerary);
            FeedBackDialog dialog = new FeedBackDialog();
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), "FeedbackDialog");
        });

    }

    private void checkSettingsAndStartLocationUpdates(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create()).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {

            IterController controller = IterController.getInstance();
            controller.goToActivity(ItineraryDetailActivity.this, FollowItineraryActivity.class, itinerary);
        });

        locationSettingsResponseTask.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException){
                ResolvableApiException apiException = (ResolvableApiException) e;
                try {
                    apiException.startResolutionForResult(this, 10001);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private void showAlertGpsPermissionNeeded(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.required_permission));
        alertDialogBuilder.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_warning));
        alertDialogBuilder.setMessage(getString(R.string.required_permission_description));
        alertDialogBuilder.setPositiveButton(getString(R.string.open_settings), (dialogInterface, i) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            this.startActivity(intent);
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void askLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                showAlertGpsPermissionNeeded();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
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


}
