package com.ingsw2122_n_03.natour.presentation.support;

import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract void onSuccess(String msg);
    public abstract void onFail(String msg);

    public String getText(TextInputEditText textInputEditText) {
        return Objects.requireNonNull(textInputEditText.getText()).toString();
    }

    public String getText(PinView pinView) {
        return Objects.requireNonNull(pinView.getText()).toString();
    }
}
