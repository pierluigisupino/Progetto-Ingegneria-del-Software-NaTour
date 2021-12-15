package com.ingsw2122_n_03.natour.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ingsw2122_n_03.natour.databinding.FragmentAddItinerary4Binding;

import java.util.ArrayList;

public class AddItineraryFragment4 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";
    private static final String ARG_PARAM6 = "param6";

    private FragmentAddItinerary4Binding binding;
    private ArrayList<byte[]> imagesBytes;

    public AddItineraryFragment4() {}

    public static AddItineraryFragment4 newInstance(String name, String description, String difficulty, int hours, int minutes, ArrayList<byte[]> imagesBytes) {
        AddItineraryFragment4 fragment = new AddItineraryFragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, name);
        args.putString(ARG_PARAM2, description);
        args.putString(ARG_PARAM3, difficulty);
        args.putString(ARG_PARAM4, String.valueOf(hours));
        args.putString(ARG_PARAM5, String.valueOf(minutes));
        args.putSerializable(ARG_PARAM6, imagesBytes);
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
            imagesBytes = (ArrayList<byte[]>) getArguments().getSerializable(ARG_PARAM6);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddItinerary4Binding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}