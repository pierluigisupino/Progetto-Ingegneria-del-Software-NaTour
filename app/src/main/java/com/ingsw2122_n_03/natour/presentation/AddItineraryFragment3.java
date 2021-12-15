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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ingsw2122_n_03.natour.databinding.Fragment3AddItineraryBinding;
import com.ingsw2122_n_03.natour.presentation.support.ImageAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddItineraryFragment3 extends Fragment {

    private Fragment3AddItineraryBinding binding;

    private RecyclerView recyclerView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private final ArrayList<Bitmap> imagesBitmap = new ArrayList<>();

    private ImageButton imageButtonLeft;
    private ImageButton imageButtonRight;

    private final ActivityResultLauncher<Intent> getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    assert data != null;
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

                    recyclerView.setAdapter(new ImageAdapter(imagesBitmap));
                    if(recyclerView.getAdapter().getItemCount() > 1) {
                        imageButtonRight.setVisibility(View.VISIBLE);
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
            String name = getArguments().getString(ARG_PARAM1);
            String description = getArguments().getString(ARG_PARAM2);
            String difficulty = getArguments().getString(ARG_PARAM3);
            String hours = getArguments().getString(ARG_PARAM4);
            String minutes = getArguments().getString(ARG_PARAM5);
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment3AddItineraryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        View view1 = getView();
        Button selectPhotoButton = binding.selectPhotoButton;

        imageButtonLeft = binding.imageButtonLeft;
        imageButtonRight = binding.imageButtonRight;
        imageButtonLeft.setVisibility(View.INVISIBLE);
        imageButtonRight.setVisibility(View.INVISIBLE);


        recyclerView = binding.image;
        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        selectPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            getImages.launch(intent);
        });

        imageButtonRight.setOnClickListener(v -> {
            int next = layoutManager.findFirstCompletelyVisibleItemPosition() +1;
            layoutManager.scrollToPosition(next);
            imageButtonLeft.setVisibility(View.VISIBLE);
            if(layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.findFirstCompletelyVisibleItemPosition()) {
                imageButtonRight.setVisibility(View.INVISIBLE);
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
        byte[] bytes;

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();

        return bytes;
    }
}