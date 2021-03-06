package com.ingsw2122_n_03.natour.presentation.itinerary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.databinding.ActivityFollowItineraryBinding;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;
import com.ingsw2122_n_03.natour.presentation.support.ImageUtilities;
import com.ingsw2122_n_03.natour.presentation.support.PointOfInterest;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FollowItineraryActivity extends BaseActivity implements Marker.OnMarkerClickListener {

    private ActivityFollowItineraryBinding binding;

    private MapView map;
    private RoadManager roadManager;
    private Polyline itineraryRoadOverlay;
    private Polyline myRoadOverlay;
    private MyLocationNewOverlay myLocationNewOverlay;

    private final HashMap<byte[], GeoPoint> uploadingPhoto = new HashMap<>();

    private final ArrayList<GeoPoint> itineraryWaypoints = new ArrayList<>();

    private final ArrayList<Marker> itineraryIndications = new ArrayList<>();
    private final ArrayList<Marker> myRoadIndications = new ArrayList<>();

    private IterController iterController;
    private LocationManager locationManager;

    private ProgressBar progressBar;
    private LinearProgressIndicator bottomProgressBar;
    private CardView cardView;

    private ConstraintLayout mainLayout;
    private LinearLayout directionsLayout;
    private LinearLayout toStartLayout;

    private SwitchMaterial toStartSwitchMaterial;
    private SwitchMaterial directionsSwitchMaterial;

    private final ActivityResultLauncher<Intent> getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    bottomProgressBar.setVisibility(View.VISIBLE);
                    new Thread(() -> {

                        assert result.getData() != null;
                        Uri imageUri = result.getData().getData();
                        ImageUtilities imageUtilities = new ImageUtilities();

                        try {

                            byte[] photoByte = imageUtilities.getBytes(this, imageUri);
                            double[] photoPosition = imageUtilities.getImageLocation(photoByte);
                            if(photoPosition != null) {
                                GeoPoint photoPoint = new GeoPoint(photoPosition[0], photoPosition[1]);
                                if(arePositionsCorrect(photoPoint)){
                                    uploadingPhoto.put(photoByte, photoPoint);
                                    iterController.uploadPhoto(photoByte);
                                }else{
                                    onFail(getString(R.string.photo_position_error));
                                }
                            }else
                                iterController.uploadPhoto(photoByte);

                        } catch (IOException e) {
                            onFail(getString(R.string.generic_error));
                        }

                    }).start();

                }else if(result.getResultCode() != Activity.RESULT_CANCELED){
                    onFail(getString(R.string.generic_error));
                }
            });


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    checkSettingsAndStartLocationUpdates();
                } else {
                    toStartSwitchMaterial.setChecked(false);
                    showAlertGpsPermissionNeeded();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        binding = ActivityFollowItineraryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        iterController = IterController.getInstance();
        iterController.setFollowItineraryActivity(this);

        Itinerary itinerary = (Itinerary) getIntent().getSerializableExtra("itinerary");
        itineraryWaypoints.add(new GeoPoint(itinerary.getStartPoint().getLatitude(), itinerary.getStartPoint().getLongitude()));
        for(WayPoint w : itinerary.getWayPoints()) {
            itineraryWaypoints.add(new GeoPoint(w.getLatitude(), w.getLongitude()));
        }

        MaterialToolbar materialToolbar = binding.topAppBar;
        materialToolbar.setTitle(itinerary.getName());

        progressBar = binding.progressBar;
        bottomProgressBar = binding.bottomProgressBar;
        cardView = binding.cardView;

        directionsSwitchMaterial = binding.directionsSwitch;
        toStartSwitchMaterial = binding.toStartSwitch;

        mainLayout = binding.mainLayout;
        directionsLayout = binding.directionsLayout;
        toStartLayout = binding.toStartLayout;
        FloatingActionButton addPhotoButton = binding.addPhotoButton;

        materialToolbar.setNavigationOnClickListener(v -> finish());

        directionsSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                Toast.makeText(view.getContext(), getString(R.string.info_directions_text), Toast.LENGTH_SHORT).show();
                map.getOverlays().addAll(itineraryIndications);
                if(toStartSwitchMaterial.isChecked()) map.getOverlays().addAll(myRoadIndications);
            }else{
                map.getOverlays().removeAll(itineraryIndications);
                map.getOverlays().removeAll(myRoadIndications);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        toStartSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    askLocationPermission();
                    toStartSwitchMaterial.setChecked(false);
                }else {
                    LocationManager lm = (LocationManager) context.getSystemService(Context. LOCATION_SERVICE);

                    if(lm.isProviderEnabled(LocationManager. GPS_PROVIDER)){
                        bottomProgressBar.setVisibility(View.VISIBLE);

                        if(myLocationNewOverlay.getMyLocation() != null){
                            makeIndicationToStartingPoint();
                        }else{
                            myLocationNewOverlay.runOnFirstFix(this::makeIndicationToStartingPoint);
                        }
                    }else{
                        buildAlertMessageNoGps();
                    }
                }

            }else{
                map.getController().animateTo(itineraryWaypoints.get(0));
                map.getOverlays().remove(myRoadOverlay);
                map.getOverlays().removeAll(myRoadIndications);
                bottomProgressBar.setVisibility(View.INVISIBLE);
            }
        });

        addPhotoButton.setOnClickListener(view1 -> {
            uploadingPhoto.clear();
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            getImages.launch(intent1);
        });

        setupMap();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(map != null) map.onPause();
    }


    /***********
     * MAP UTILS
     ***********/

    @SuppressLint("MissingPermission")
    private void setupMap() {

        map = binding.map;
        map.setClickable(true);
        map.setMultiTouchControls(true);

        map.getController().setZoom(12.0);
        map.getController().animateTo(itineraryWaypoints.get(0));
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        roadManager = new OSRMRoadManager(this, null);
        ((OSRMRoadManager)roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);

        addMarker(itineraryWaypoints.get(0), true);
        if(itineraryWaypoints.size() > 1)
            addMarker(itineraryWaypoints.get(itineraryWaypoints.size()-1), false);

        addPointOfInterests(iterController.calculatePhotosPosition());

        makeRoads();

        GpsMyLocationProvider gpsMyLocationProvider = new GpsMyLocationProvider(this);

        myLocationNewOverlay = new MyLocationNewOverlay(gpsMyLocationProvider, map);
        myLocationNewOverlay.enableMyLocation();

        map.getOverlays().add(myLocationNewOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        scaleBarOverlay.setCentred(true);

        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(scaleBarOverlay);
    }


    private void addMarker(GeoPoint point, boolean isFirst){

        Marker marker = new Marker(map);

        if(isFirst){
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_start, null));
            marker.setTitle(getString(R.string.start_point_text));
        }else {
            marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_circle_finish, null));
            marker.setTitle(getString(R.string.end_point_text));
        }

        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setPosition(point);

        map.getOverlays().add(marker);
        map.invalidate();

    }


    private void addPointOfInterests(HashMap<byte[], GeoPoint> imagesPosition){

        for (Map.Entry<byte[], GeoPoint> entry : imagesPosition.entrySet()) {

            byte[] imageBytes = entry.getKey();

            BitmapDrawable drawable = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length));
            PointOfInterest pointOfInterest = new PointOfInterest(map, imageBytes);

            pointOfInterest.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image, null));
            pointOfInterest.setImage(drawable);

            pointOfInterest.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            pointOfInterest.setPosition(entry.getValue());

            pointOfInterest.setOnMarkerClickListener(this);

            map.getOverlays().add(pointOfInterest);

        }

    }


    @Override
    public boolean onMarkerClick(Marker marker, MapView mapView) {

        if(marker instanceof PointOfInterest) {
            Dialog builder = new Dialog(this);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

            builder.setOnDismissListener(dialogInterface -> {});

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(marker.getImage());
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
            );
            builder.show();

        }

        return true;

    }


    /*****************
     * ROAD DIRECTIONS
     *****************/

    private void makeRoads(){

        new Thread(()-> {

            Road road = roadManager.getRoad(itineraryWaypoints);
            itineraryRoadOverlay = RoadManager.buildRoadOverlay(road);
            itineraryRoadOverlay.getOutlinePaint().setStrokeWidth(8);

            makeDirections(road, itineraryIndications);
            map.getOverlays().add(itineraryRoadOverlay);

            cardView.post(() -> {
                progressBar.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);

                directionsLayout.setVisibility(View.VISIBLE);

                if(locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)){
                    toStartLayout.setVisibility(View.VISIBLE);
                }
            });

        }).start();
    }


    private void makeIndicationToStartingPoint(){
        new Thread(()-> {
            ArrayList<GeoPoint> myRoadWaypoints = new ArrayList<>(itineraryWaypoints);

            if(!myRoadWaypoints.contains(myLocationNewOverlay.getMyLocation()))
                myRoadWaypoints.add(0, myLocationNewOverlay.getMyLocation());

            Road road = roadManager.getRoad(myRoadWaypoints);
            myRoadOverlay = RoadManager.buildRoadOverlay(road);
            myRoadOverlay.getOutlinePaint().setStrokeWidth(8);

            if(toStartSwitchMaterial.isChecked()) {
                makeDirections(road, myRoadIndications);
                map.getOverlays().add(myRoadOverlay);
            }

            cardView.post(() -> {
                if(toStartSwitchMaterial.isChecked()) {
                    map.getController().setZoom(16.50);
                    map.getController().animateTo(myLocationNewOverlay.getMyLocation());
                }

                if(directionsSwitchMaterial.isChecked()) map.getOverlays().addAll(myRoadIndications);

                bottomProgressBar.setVisibility(View.INVISIBLE);
            });
        }).start();
    }


    public void makeDirections(Road road, ArrayList<Marker> indications) {

        indications.clear();

        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_directions, null));
            nodeMarker.setTitle("Step " + i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this, node.mLength, node.mDuration));
            indications.add(nodeMarker);
        }
    }


    /**************
     * PERMISSIONS
     *************/

    private void checkSettingsAndStartLocationUpdates(){
        LocationSettingsRequest request = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create()).build();

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(locationSettingsResponse -> {
            bottomProgressBar.setVisibility(View.VISIBLE);
            myLocationNewOverlay.runOnFirstFix(this::makeIndicationToStartingPoint);
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
                toStartSwitchMaterial.setChecked(false);
                showAlertGpsPermissionNeeded();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }


    /*********************
     * PHOTO UPLOAD UTILS
     *******************/

    public boolean arePositionsCorrect(GeoPoint position) {

        if(itineraryWaypoints.size() > 1) {

           float tolerance = map.getProjection().metersToPixels(100);
           GeoPoint closest = itineraryRoadOverlay.getCloseTo(position, tolerance, map);

           return closest != null;

        }else
            return !(position.distanceToAsDouble(itineraryWaypoints.get(0)) > 10000);

    }


    public void onPhotoUploadFinish(boolean success) {
        if(success){
            if(!uploadingPhoto.isEmpty())
                addPointOfInterests(uploadingPhoto);
            onSuccess(getString(R.string.photo_added_success));
        }else
            onFail(getString(R.string.photo_upload_failed));
    }


    /****************
     * BASE ACTIVITY
     ***************/

    @Override
    public void onSuccess(String msg) {

        runOnUiThread(() -> {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(mainLayout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(FollowItineraryActivity.this, R.color.success));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

    @Override
    public void onFail(String msg) {

        runOnUiThread(() -> {
            bottomProgressBar.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(mainLayout, msg, Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(ContextCompat.getColor(FollowItineraryActivity.this, R.color.error));

            TextView tv = (snackbar.getView()).findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.euclid_circular_regular);
            tv.setTypeface(typeface);

            snackbar.show();
        });
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    toStartSwitchMaterial.setChecked(false);
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("No", (dialog, id) -> {
                    toStartSwitchMaterial.setChecked(false);
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
