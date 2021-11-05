package com.ingsw2122_n_03.natour.presentation;

import com.chaos.view.PinView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VerifyAccount extends BaseActivity {

    private Controller startController;

    private MaterialToolbar materialToolbar;
    private PinView verificationCodePinView;
    private TextView resendCodeTextView;
    private Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        startController = Controller.getInstance();

        materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);

        TextInputEditText usernameEditText = (TextInputEditText) findViewById(R.id.username);
        verificationCodePinView = (PinView) findViewById(R.id.verificationCode);
        resendCodeTextView = (TextView) findViewById(R.id.resendCode);
        verifyButton = (Button) findViewById(R.id.verifyButton);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        resendCodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                startController.sendVerificationCode(username);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                String verificationCode = verificationCodePinView.getText().toString();
                startController.confirmSignUp(VerifyAccount.this, username, verificationCode);
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