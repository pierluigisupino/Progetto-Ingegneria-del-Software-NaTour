package com.ingsw2122_n_03.natour.presentation;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.ingsw2122_n_03.natour.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddItineraryFragment3 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private String name;
    private String description;
    private String difficulty;
    private String hours;
    private String minutes;
    private ArrayList<Bitmap> imagesBitmap = new ArrayList<>();

    private View view;
    private Button selectPhotoButton;

    private final ActivityResultLauncher<Intent> getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            imagesBitmap.add(getImageBitmap(imageUri));
                        }
                    } else {
                        Uri imageUri = data.getData();
                        imagesBitmap.add(getImageBitmap(imageUri));
                    }
                }
            });

    public AddItineraryFragment3() {}

    public static AddItineraryFragment3 newInstance(String name, String description, String difficulty, int hours, int minutes) {
        AddItineraryFragment3 fragment = new AddItineraryFragment3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, difficulty);
        args.putString(ARG_PARAM4, String.valueOf(hours));
        args.putString(ARG_PARAM5, String.valueOf(minutes));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            description = getArguments().getString(ARG_PARAM2);
            difficulty = getArguments().getString(ARG_PARAM3);
            hours = getArguments().getString(ARG_PARAM4);
            minutes = getArguments().getString(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_3_add_itinerary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        this.view = getView();
        selectPhotoButton = view.findViewById(R.id.selectPhotoButton);

        selectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                getImages.launch(intent);
            }
        });
    }

    private Bitmap getImageBitmap(Uri imageUri){

        Bitmap bitmap = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), imageUri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
}

    private byte[] getImageBytes(Bitmap imageBitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = null;

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();

        return bytes;
    }
}