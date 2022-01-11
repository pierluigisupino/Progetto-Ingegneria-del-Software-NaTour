package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.databinding.Fragment1AddItineraryBinding;


public class AddItineraryFragment1 extends Fragment {

    private Fragment1AddItineraryBinding binding;

    private TextInputLayout nameTextInputLayout;
    private EditText nameEditText;
    private EditText descriptionEditText;

    private boolean isFirstSubmit = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = Fragment1AddItineraryBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameTextInputLayout = binding.nameTextInputLayout;
        nameEditText = binding.nameEditText;

        descriptionEditText = binding.descriptionEditText;

        nameEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isNameValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public boolean isNameValid(){

        isFirstSubmit = false;

        if(nameEditText.getText() == null || nameEditText.getText().length() == 0) {
            nameTextInputLayout.setError(getString(R.string.name_warning));
            return false;
        }else if(nameEditText.getText().toString().matches("\\s+.*")){
            nameTextInputLayout.setError(getString(R.string.name_space_warning));
            return false;
        }

        nameTextInputLayout.setError(null);
        return true;
    }

    public String getName(){
        return nameEditText.getText().toString();
    }

    public String getDescription(){
        return descriptionEditText.getText().toString();
    }
}