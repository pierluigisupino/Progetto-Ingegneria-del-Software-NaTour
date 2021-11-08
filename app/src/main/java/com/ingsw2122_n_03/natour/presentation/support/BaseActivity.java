package com.ingsw2122_n_03.natour.presentation.support;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputEditText;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract void onSuccess(String snackbarMessage);
    public abstract void onFail(String snackbarMessage);

    public String getText(TextInputEditText textInputEditText) {
        return textInputEditText.getText().toString();
    }

    public String getText(PinView pinView) {
        return pinView.getText().toString();
    }
}
