package com.ingsw2122_n_03.natour.presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.ingsw2122_n_03.natour.R;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    public void startLoading() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
