package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.Fragment3AddItineraryBinding;
import com.ingsw2122_n_03.natour.presentation.support.ImageAdapter;
import com.ingsw2122_n_03.natour.presentation.support.ImageUtilities;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class AddItineraryFragment3 extends Fragment {

    private Fragment3AddItineraryBinding binding;
    private RecyclerView recyclerView;
    private final AddItineraryActivity addItineraryActivity;

    private final ArrayList<byte[]> imagesBytes = new ArrayList<>();
    private final HashMap<byte[], GeoPoint> pointOfInterests = new HashMap<>();

    private TextView countImageTextView;

    private ActivityResultLauncher<Intent> getImages;
    private ImageUtilities imageUtilities;

    private final int photoCount = 5;

    public AddItineraryFragment3(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment3AddItineraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countImageTextView = binding.photoTextView2;
        Button selectPhotoButton = binding.selectPhotoButton;
        recyclerView = binding.image;
        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        imageUtilities = new ImageUtilities();

        getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        addItineraryActivity.showProgressBar();
                        new Thread(() -> {
                            Intent data = result.getData();
                            assert data != null;
                            ClipData clipData = data.getClipData();
                            if (clipData != null && imagesBytes.size() < photoCount) {

                                if(clipData.getItemCount() > photoCount || imagesBytes.size() + clipData.getItemCount() > photoCount){
                                    addItineraryActivity.onFail(getString(R.string.photo_limit));
                                }

                                for (int i = 0; (i < clipData.getItemCount()) & (imagesBytes.size() < photoCount); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();

                                    try {

                                        byte[] photoByte = imageUtilities.getBytes(requireActivity(), imageUri);
                                        imagesBytes.add(photoByte);

                                        double[] coordinates = imageUtilities.getImageLocation(photoByte);
                                        if(coordinates != null) pointOfInterests.put(photoByte, new GeoPoint(coordinates[0], coordinates[1]));

                                    }catch (IOException e) {
                                        addItineraryActivity.onFail(getString(R.string.generic_error));
                                        break;
                                    }

                                }

                            }else if(imagesBytes.size() == photoCount){
                                addItineraryActivity.onFail(getString(R.string.photo_limit));
                            }

                            recyclerView.post(() -> {
                                setAdapter();
                                addItineraryActivity.hideProgressBar();
                            });
                        }).start();

                    }else if(result.getResultCode() != Activity.RESULT_CANCELED){
                        addItineraryActivity.onFail(getString(R.string.generic_error));
                    }
                });

        selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            getImages.launch(intent);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    private void setAdapter(){
        recyclerView.setAdapter(new ImageAdapter(countImageTextView, imagesBytes, pointOfInterests));
    }

    public ArrayList<byte[]> getImagesBytes(){ return this.imagesBytes; }

    public HashMap<byte[], GeoPoint> getPointOfInterest(){
        return this.pointOfInterests;
    }

}