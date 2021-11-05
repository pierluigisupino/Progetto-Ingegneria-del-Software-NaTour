package com.ingsw2122_n_03.natour.presentation.support;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public abstract void onSuccess(String snackbarMessage);
    public abstract void onFail(String snackbarMessage);

}
