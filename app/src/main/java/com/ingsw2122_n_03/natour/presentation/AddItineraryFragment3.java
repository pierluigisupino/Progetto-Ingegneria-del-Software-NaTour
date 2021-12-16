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
import java.util.Iterator;

public class AddItineraryFragment3 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private Fragment3AddItineraryBinding binding;
    private RecyclerView recyclerView;
    private AddItineraryActivity addItineraryActivity;

    private final ArrayList<Bitmap> imagesBitmap = new ArrayList<>();
    private final ArrayList<byte[]> imagesBytes = new ArrayList<>();

    private TextView textView;

    private final ActivityResultLauncher<Intent> getImages = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
                            }
                        } else {
                            Uri imageUri = data.getData();
                            Bitmap bitmap = createImageBitmap(imageUri);
                            imagesBitmap.add(bitmap);
                        }
                        recyclerView.post(() -> {
                            setAdapter();
                            addItineraryActivity.onSuccess(null);
                            //addItineraryActivity.onSuccess(getString(R.string.photo_added_success));
                        });
                    }).start();
                }else {
                    addItineraryActivity.onFail(getString(R.string.generic_error));
                }
            });

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

        textView = binding.photoTextView2;
        Button selectPhotoButton = binding.selectPhotoButton;
        recyclerView = binding.image;
        LinearLayoutManager layoutManager =  new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        textView.setText(getString(R.string.no_photo_selected_text));

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
        recyclerView.setAdapter(new ImageAdapter(textView, imagesBitmap));

        // TODO: 15/12/2021
        if(imagesBytes.size() != 0)
            textView.setText(imagesBytes.size()+" "+getString(R.string.photo_selected_text));

        recyclerView.setAdapter(new ImageAdapter(textView, imagesBitmap));
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
        Iterator<Bitmap> iterator = imagesBitmap.iterator();
        while(iterator.hasNext()) {
            imagesBytes.add(createImageBytes(iterator.next()));
        }
        return this.imagesBytes;
    }
}