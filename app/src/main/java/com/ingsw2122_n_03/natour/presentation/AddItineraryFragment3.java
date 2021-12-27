package com.ingsw2122_n_03.natour.presentation;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AddItineraryFragment3 extends Fragment {

    private Fragment3AddItineraryBinding binding;
    private RecyclerView recyclerView;
    private final AddItineraryActivity addItineraryActivity;

    private final ArrayList<Bitmap> imagesBitmap = new ArrayList<>();
    private final ArrayList<byte[]> imagesBytes = new ArrayList<>();

    private TextView countImageTextView;

    private ActivityResultLauncher<Intent> getImages;

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

        getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        addItineraryActivity.showProgressBar();
                        new Thread(() -> {
                            Intent data = result.getData();
                            assert data != null;
                            ClipData clipData = data.getClipData();
                            if (clipData != null) {
                                for (int i = 0; i < clipData.getItemCount(); i++) {
                                    Uri imageUri = clipData.getItemAt(i).getUri();
                                    Bitmap bitmap = createImageBitmap(imageUri);
                                    imagesBitmap.add(bitmap);
                                    new Thread(() -> imagesBytes.add(createImageBytes(bitmap))).start();
                                }
                            } else {
                                Uri imageUri = data.getData();
                                Bitmap bitmap = createImageBitmap(imageUri);
                                imagesBitmap.add(bitmap);
                                new Thread(() -> imagesBytes.add(createImageBytes(bitmap))).start();
                            }
                            recyclerView.post(() -> {
                                setAdapter();
                                addItineraryActivity.onSuccess(null);
                            });
                        }).start();
                    }else {
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
        recyclerView.setAdapter(new ImageAdapter(countImageTextView, imagesBitmap));
    }

    private Bitmap createImageBitmap(Uri imageUri){

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

    private byte[] createImageBytes(Bitmap imageBitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes;

        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        bytes = byteArrayOutputStream.toByteArray();

        return bytes;
    }

    public ArrayList<byte[]> getImagesBytes(){

        /*for(Bitmap bitmap : imagesBitmap){
            imagesBytes.add(createImageBytes(bitmap));
        }*/

        return this.imagesBytes;
    }

}