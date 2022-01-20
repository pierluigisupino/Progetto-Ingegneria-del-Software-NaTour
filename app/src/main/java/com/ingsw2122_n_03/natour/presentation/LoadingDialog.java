package com.ingsw2122_n_03.natour.presentation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ingsw2122_n_03.natour.R;

public class LoadingDialog {

    private final Activity activity;
    private AlertDialog dialog;
    private final String loadingMessage;

    public LoadingDialog(Activity activity, String loadingMessage){
        this.loadingMessage = loadingMessage;
        this.activity = activity;
    }

    @SuppressLint("InflateParams")
    public void startLoading() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_loading, null);
        TextView loadingText = view.findViewById(R.id.loadingTextView);
        loadingText.setText(loadingMessage);
        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
