package com.ingsw2122_n_03.natour.presentation;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.ingsw2122_n_03.natour.R;

import java.util.Objects;

public class AddItineraryFragment1 extends Fragment {

    private TextInputLayout nameTextInputLayout;
    private EditText nameEditText;

    private TextInputLayout descriptionTextInputLayout;
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

        nameTextInputLayout = getView().findViewById(R.id.nameTextInputLayout);
        nameEditText = getView().findViewById(R.id.nameEditText);

        descriptionTextInputLayout = getView().findViewById(R.id.descriptionTextInputLayout);
        descriptionEditText = getView().findViewById(R.id.descriptionEditText);

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
        }

        nameEditText.setError(null);
        return true;
    }

    public String getName(){
        return nameEditText.getText().toString();
    }

    public String getDescription(){
        return descriptionEditText.getText().toString();
    }
}