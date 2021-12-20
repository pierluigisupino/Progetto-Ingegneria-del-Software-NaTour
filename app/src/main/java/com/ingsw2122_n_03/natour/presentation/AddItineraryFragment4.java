package com.ingsw2122_n_03.natour.presentation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ingsw2122_n_03.natour.databinding.Fragment4AddItineraryBinding;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.ticofab.androidgpxparser.parser.GPXParser;
import io.ticofab.androidgpxparser.parser.domain.Gpx;
import io.ticofab.androidgpxparser.parser.domain.WayPoint;

public class AddItineraryFragment4 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    private Fragment4AddItineraryBinding binding;
    private FloatingActionButton addGPX;

    private ActivityResultLauncher<Intent> getGPX;
    private AddItineraryActivity addItineraryActivity;

    public AddItineraryFragment4(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Fragment4AddItineraryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addGPX = binding.addGPXbutton;

        getGPX = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                String path = data.getData().getPath();

                GPXParser mParser = new GPXParser();
                Gpx parsedGpx = null;

                try {
                    File file = new File(path);
                    InputStream in = new FileInputStream(file);
                    parsedGpx = mParser.parse(in);
                } catch (IOException | XmlPullParserException e) {
                    Log.e("GPX test", e.toString());
                }

                if (parsedGpx != null) {
                    List<WayPoint> wayPoints = parsedGpx.getWayPoints();

                    for(WayPoint wayPoint : wayPoints){
                        Log.e("NaToure",wayPoint.getLatitude()+ " " + wayPoint.getLongitude() + " " + wayPoint.getElevation());
                    }
                } else {
                    Log.e("NaToure", "Error parsing gpx track!");
                }
            }
        });

        addGPX.setOnClickListener(v -> {
            Intent data = new Intent(Intent.ACTION_GET_CONTENT);
            data.addCategory(Intent.CATEGORY_OPENABLE);
            data.setType("*/*");
            Intent intent = Intent.createChooser(data, "Choose a file");
            getGPX.launch(intent);
        });
    }
}