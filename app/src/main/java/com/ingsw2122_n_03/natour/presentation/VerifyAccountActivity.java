package com.ingsw2122_n_03.natour.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chaos.view.PinView;
import com.google.android.material.appbar.MaterialToolbar;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class VerifyAccountActivity extends BaseActivity {

    private AuthController authController;

    private PinView verificationCodePinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        Intent intent = getIntent();
        String username = intent.getExtras().getString("username");
        String password = intent.getExtras().getString("password");

        authController = AuthController.getInstance();
        authController.setVerifyAccountActivity(VerifyAccountActivity.this);

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        verificationCodePinView = (PinView) findViewById(R.id.verificationCode);
        TextView resendCodeTextView = (TextView) findViewById(R.id.resendCode);
        Button verifyButton = (Button) findViewById(R.id.verifyButton);

        resendCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT SU USERNAME
                authController.sendVerificationCode(username);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String verificationCode = verificationCodePinView.getText().toString();
                authController.confirmSignUp(username, password, verificationCode);
            }
        });
    }

    @Override
    public void onSuccess(String msg) {
        //SHOW SNACKBAR
    }

    @Override
    public void onFail(String msg) {
        //SHOW SNACKBAR
    }
}