package com.ingsw2122_n_03.natour.presentation;

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


public class AddItineraryFragment1 extends Fragment {

    private TextInputLayout nameTextInputLayout;
    private EditText nameEditText;

    private EditText descriptionEditText;

    private boolean isFirstSubmit = true;

    public AddItineraryFragment1() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1_add_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        nameTextInputLayout = view.findViewById(R.id.nameTextInputLayout);
        nameEditText = view.findViewById(R.id.nameEditText);

        //TextInputLayout descriptionTextInputLayout = view.findViewById(R.id.descriptionTextInputLayout);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);

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

        if(nameEditText.getText() == null || nameEditText.getText().length() == 0 || nameEditText.getText().toString().matches("\\s+")) {
            nameTextInputLayout.setError(getString(R.string.name_warning));
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