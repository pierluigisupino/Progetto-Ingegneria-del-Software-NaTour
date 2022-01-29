package com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

        descriptionEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        descriptionEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                String text = descriptionEditText.getText().toString();
                InputFilter filter = (source, start1, end, dest, d_start, d_end) -> {
                    String stringSource = source.toString();
                    String stringDest = dest.toString();
                    if (stringSource.equals(" ")) {
                        if (stringDest.length() == 0)
                            return "";
                        else
                            if ((d_start > 0 && text.charAt(d_start - 1) == ' ') || (text.length() >  d_start && text.charAt(d_start) == ' ') || d_start == 0)
                                return "";
                    }
                    return null;
                };
                descriptionEditText.setFilters(new InputFilter[]{filter});
            }

            @Override
            public void afterTextChanged(Editable s) { }

        });

        nameEditText.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isFirstSubmit) isNameValid();
            }

            @Override
            public void afterTextChanged(Editable s) { }

        });
    }


    public boolean isNameValid(){

        isFirstSubmit = false;

        if(nameEditText.getText() == null || nameEditText.getText().length() == 0) {
            nameTextInputLayout.setError(getString(R.string.name_warning));
            return false;
        }else if(nameEditText.getText().toString().matches("\\s+.*")) {
            nameTextInputLayout.setError(getString(R.string.name_space_warning));
            return false;
        }else if(nameEditText.getText().length() < 4) {
            nameTextInputLayout.setError(getString(R.string.username_length_error));
            return false;
        }

        nameTextInputLayout.setError(null);
        return true;
    }


    public String getName(){
        String name = nameEditText.getText().toString();
        name = name.trim();
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    public String getDescription(){

        String description = descriptionEditText.getText().toString();

        if(description.length() > 0) {
            description = description.trim();
            description = description.substring(0,1).toUpperCase()+description.substring(1);
            if(description.charAt(description.length() - 1) != '.')
                description = description + ".";
        }

        return description;
    }

}